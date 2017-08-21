<?php

    // configuration
    require("../includes/config.php"); 
    
    // load the 'portfolio' table data for the current user
    $rows = CS50::query("SELECT * FROM portfolio WHERE user_id = ?", $_SESSION["id"]);
    
    // store the data in a new array
    $positions = [];
    foreach ($rows as $row)
    {
        $stock = lookup($row["symbol"]);
        if ($stock != false)
        {
            $positions[] = [
                "name" => $stock["name"],
                "price" => number_format($stock["price"], 4),
                "shares" => $row["shares"],
                "symbol" => $row["symbol"],
                "total" => number_format($stock["price"] * $row["shares"], 4)
            ];
        }
    }
    
    // get the value of 'cash'
    $cash = CS50::query("SELECT cash FROM users WHERE id = ?", $_SESSION["id"]);
    $cash = number_format($cash[0]['cash'], 4);
    
    // render portfolio
    render("portfolio.php", ["title" => "Portfolio", "positions" => $positions, "cash" => $cash]);

?>
