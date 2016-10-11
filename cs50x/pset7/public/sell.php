<?php

    // configuration
    require("../includes/config.php"); 
    
    // if user reached page via GET (as by clicking a link or via redirect)
    if ($_SERVER["REQUEST_METHOD"] == "GET")
    {
        // load the owned symbols
        $symbols = CS50::query("SELECT symbol FROM portfolio WHERE user_id=?", $_SESSION["id"]);
        
        // render form
        render("sell_input.php", ["title" => "Sell", "symbols" => $symbols]);
    }
    
    // else if user reached page via POST (as by submitting a form via POST)
    else if ($_SERVER["REQUEST_METHOD"] == "POST")
    {
        // validate submission
        if (empty($_POST["symbol"]))
        {
            apologize("You must provide a symbol");
        }
        
        // get the number of shares
        $shares = CS50::query("SELECT shares FROM portfolio WHERE user_id=? AND symbol=?", $_SESSION["id"], $_POST["symbol"]);
        
        // check if user owns any shares
        if (!$shares)
        {
            redirect("/");
        }
        $shares = $shares[0]['shares'];
        
        // get the current price of the shares
        $price = lookup($_POST["symbol"]);
        $price = $price["price"];
                
        // get the value of cash
        $cash = CS50::query("SELECT cash FROM users WHERE id = ?", $_SESSION["id"]);
        $cash = $cash[0]['cash'];
        
        // update the user's cash
        $cash = $cash + ($shares * $price);
        CS50::query("UPDATE users SET cash = ? WHERE id = ?", $cash, $_SESSION["id"]);
        
        // update portfolio
        CS50::query("DELETE FROM portfolio WHERE user_id = ? AND symbol = ?", $_SESSION["id"], $_POST["symbol"]);
        
        // update history
        CS50::query("INSERT INTO history VALUES(?, ?, CURRENT_TIMESTAMP, ?, ?, ?)", $_SESSION["id"], "SELL", strtoupper($_POST["symbol"]), $shares, $price);
        
        redirect("/");
    }

?>