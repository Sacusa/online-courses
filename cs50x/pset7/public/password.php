<?php

    // configuration
    require("../includes/config.php"); 
    
    // if user reached page via GET (as by clicking a link or via redirect)
    if ($_SERVER["REQUEST_METHOD"] == "GET")
    {
        // render form
        render("password_input.php", ["title" => "Change Password"]);
    }
    
    // else if user reached page via POST (as by submitting a form via POST)
    else if ($_SERVER["REQUEST_METHOD"] == "POST")
    {
        // validate submission
        if (empty($_POST["current"]))
        {
            apologize("You must enter the current password");
        }
        else if (empty($_POST["new"]))
        {
            apologize("You must enter a new password");
        }
        else if (empty($_POST["confirm"]))
        {
            apologize("You must re-enter the new password");
        }
        
        // get the hash of current password
        $current = CS50::query("SELECT hash FROM users WHERE id=?", $_SESSION["id"]);
        $current = $current[0]['hash'];
        
        // check the current password
        if (!password_verify($_POST["current"], $current))
        {
            apologize("Incorrect password");
        }
        
        // check if the two passwords match
        if (strcmp($_POST["new"], $_POST["confirm"]))
        {
            apologize("The two passwords do not match");
        }
        
        // store the hash of new password
        CS50::query("UPDATE users SET hash=? WHERE id=?", password_hash($_POST["new"], PASSWORD_DEFAULT), $_SESSION["id"]);
        
        redirect("/");
    }
    
?>