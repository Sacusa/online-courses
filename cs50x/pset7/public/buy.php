<?php

    // configuration
    require("../includes/config.php"); 
    
    // if user reached page via GET (as by clicking a link or via redirect)
    if ($_SERVER["REQUEST_METHOD"] == "GET")
    {
        // render form
        render("buy_input.php", ["title" => "Buy"]);
    }
    
    // else if user reached page via POST (as by submitting a form via POST)
    else if ($_SERVER["REQUEST_METHOD"] == "POST")
    {
        // validate submission
        if (empty($_POST["symbol"]))
        {
            apologize("You must enter a symbol");
        }
        else if (empty($_POST["shares"]))
        {
            apologize("You must enter the number of shares");
        }
        else if (!preg_match("/^\d+$/", $_POST["shares"]))
        {
            apologize("Invalid number of shares");
        }
        else if (!lookup($_POST["symbol"]))
        {
            apologize("Invalid stock symbol");
        }
        
        // get the price of shares
        $price = lookup($_POST["symbol"]);
        $price = $price['price'];
        
        // get the value of cash
        $cash = CS50::query("SELECT cash FROM users WHERE id = ?", $_SESSION["id"]);
        $cash = $cash[0]['cash'];
        
        // check if user has enough cash
        if (($_POST["shares"] * $price) > $cash)
        {
            apologize("You don't have enough cash");
        }
        
        // update cash
        $cash = $cash - ($_POST["shares"] * $price);
        CS50::query("UPDATE users SET cash = ? WHERE id = ?", $cash, $_SESSION["id"]);
        
        // update portfolio
        CS50::query("INSERT INTO portfolio (user_id, symbol, shares) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE shares = shares + VALUES(shares)", $_SESSION["id"], strtoupper($_POST["symbol"]), $_POST["shares"]);
        
        // update history
        CS50::query("INSERT INTO history VALUES(?, ?, CURRENT_TIMESTAMP, ?, ?, ?)", $_SESSION["id"], "BUY", strtoupper($_POST["symbol"]), $_POST["shares"], $price);
        
        redirect("/");
    }
    
?>