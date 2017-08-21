<?php

    // configuration
    require("../includes/config.php");
    
    // load all the transactions
    $rows = CS50::query("SELECT * FROM history WHERE id = ?", $_SESSION["id"]);
    
    // add prices
    foreach ($rows as $row)
    {
        $stock = lookup($row["symbol"]);
        $row["price"] = $stock["price"];
    }
    
    // display the history
    render("history_display.php", ["title" => "History", "rows" => $rows]);

?>