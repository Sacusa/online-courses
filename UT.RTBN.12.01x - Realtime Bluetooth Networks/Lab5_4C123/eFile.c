// eFile.c
// Runs on either TM4C123 or MSP432
// High-level implementation of the file system implementation.
// Daniel and Jonathan Valvano
// August 29, 2016
#include <stdint.h>
#include "eDisk.h"

uint8_t Buff[512]; // temporary buffer used during file I/O
uint8_t Directory[256], FAT[256];
int32_t bDirectoryLoaded =0; // 0 means disk on ROM is complete, 1 means RAM version active

// Return the larger of two integers.
int16_t max(int16_t a, int16_t b){
  if(a > b){
    return a;
  }
  return b;
}

//*****MountDirectory******
// if directory and FAT are not loaded in RAM,
// bring it into RAM from disk
void MountDirectory(void){ 
// if bDirectoryLoaded is 0, 
//    read disk sector 255 and populate Directory and FAT
//    set bDirectoryLoaded=1
// if bDirectoryLoaded is 1, simply return
  if (bDirectoryLoaded == 0) {
    eDisk_ReadSector(Buff, 255);
    
    for (int i = 0; i < 256; ++i) {
      Directory[i] = Buff[i];
      FAT[i] = Buff[i + 256];
    }
    
    bDirectoryLoaded = 1;
  }
}

// Return the index of the last sector in the file
// associated with a given starting sector.
// Note: This function will loop forever without returning
// if the file has no end (i.e. the FAT is corrupted).
uint8_t lastsector(uint8_t start) {
  // return if last sector
  if (start == 255) {
    return 255;
  }
  
  uint8_t sector_value = FAT[start];
  
  // get to the last sector of the file
  while (sector_value != 255) {
    start = sector_value;
    sector_value = FAT[start];
  }
	
  return start;
}

// Return the index of the first free sector.
// Note: This function will loop forever without returning
// if a file has no end or if (Directory[255] != 255)
// (i.e. the FAT is corrupted).
uint8_t findfreesector(void){
  uint8_t file_number = 0, last_sector = 0;
  int16_t free_sector = -1;
  
  last_sector = lastsector(Directory[file_number]);
  
  while (last_sector != 255) {
    free_sector = max(free_sector, last_sector);
    ++file_number;
    last_sector = lastsector(Directory[file_number]);
  }
  
  return (free_sector + 1);
}

// Append a sector index 'n' at the end of file 'num'.
// This helper function is part of OS_File_Append(), which
// should have already verified that there is free space,
// so it always returns 0 (successful).
// Note: This function will loop forever without returning
// if the file has no end (i.e. the FAT is corrupted).
uint8_t appendfat(uint8_t num, uint8_t n) {
  // special handling for new files
  if (Directory[num] == 255) {
    Directory[num] = n;
  }
  else {
    FAT[lastsector(Directory[num])] = n;
  }
  
  FAT[n] = 255;
	
  return 0;
}

//********OS_File_New*************
// Returns a file number of a new file for writing
// Inputs: none
// Outputs: number of a new file
// Errors: return 255 on failure or disk full
uint8_t OS_File_New(void){
  // mount Directory and FAT if not already
  MountDirectory();
  uint8_t dir = 0;
  
  while (1) {
    // file found; Return index
    if (Directory[dir] == 255) {
      return dir;
    }
    
    ++dir;
    
    // reached end of table; break out
    if (dir == 255) {
      break;
    }
  }
	
  return 255;
}

//********OS_File_Size*************
// Check the size of this file
// Inputs:  num, 8-bit file number, 0 to 254
// Outputs: 0 if empty, otherwise the number of sectors
// Errors:  none
uint8_t OS_File_Size(uint8_t num) {
  // base case; empty file
  if (Directory[num] == 255) {
    return 0;
  }
  
  uint8_t size = 1, sector_value = FAT[Directory[num]];
  
  while (sector_value != 255) {
    ++size;
    sector_value = FAT[sector_value];
  }
	
  return size;
}

//********OS_File_Append*************
// Save 512 bytes into the file
// Inputs:  num, 8-bit file number, 0 to 254
//          buf, pointer to 512 bytes of data
// Outputs: 0 if successful
// Errors:  255 on failure or disk full
uint8_t OS_File_Append(uint8_t num, uint8_t buf[512]){
  // mount Directory and FAT if not already
  MountDirectory();
  
  // find a free sector
  uint8_t free_sector = findfreesector();
  if (free_sector == 255) {
    return 255;
  }
  
  // write buffer to the free sector
  eDisk_WriteSector(buf, free_sector);
  appendfat(num, free_sector);
  
  return 0;
}

//********OS_File_Read*************
// Read 512 bytes from the file
// Inputs:  num, 8-bit file number, 0 to 254
//          location, logical address, 0 to 254
//          buf, pointer to 512 empty spaces in RAM
// Outputs: 0 if successful
// Errors:  255 on failure because no data
uint8_t OS_File_Read(uint8_t num, uint8_t location,
                     uint8_t buf[512]){
  uint8_t current_sector = Directory[num];
  
  // reach the desired sector
  while (location > 0) {
    current_sector = FAT[current_sector];
    --location;
  }
  
  // check if we reached the end of file
  if (current_sector == 255) {
    return 255;
  }
  
  // read sector into buffer
  eDisk_ReadSector(buf, current_sector);
  
  return 0;
}

//********OS_File_Flush*************
// Update working buffers onto the disk
// Power can be removed after calling flush
// Inputs:  none
// Outputs: 0 if success
// Errors:  255 on disk write failure
uint8_t OS_File_Flush(void){
  for (int i = 0; i < 256; ++i) {
    Buff[i] = Directory[i];
    Buff[i + 256] = FAT[i];
  }
  eDisk_WriteSector(Buff, 255);
  
  return 0;
}

//********OS_File_Format*************
// Erase all files and all data
// Inputs:  none
// Outputs: 0 if success
// Errors:  255 on disk write failure
uint8_t OS_File_Format(void){
// call eDiskFormat
// clear bDirectoryLoaded to zero
  eDisk_Format();
  bDirectoryLoaded = 0;
  return 0;
}
