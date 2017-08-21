<?php

    // configuration
    require("../includes/config.php"); 
    
    // if user reached page via GET (as by clicking a link or via redirect)
    if ($_SERVER["REQUEST_METHOD"] == "GET")
    {
        // render form
        render("quote_input.php", ["title" => "Quote"]);
    }
    
    // else if user reached page via POST (as by submitting a form via POST)
    else if ($_SERVER["REQUEST_METHOD"] == "POST")
    {
        // validate submission
        if (empty($_POST["symbol"]))
        {
            apologize("You must provide a symbol");
        }
        
        // get stock data
        $stock = lookup($_POST["symbol"]);
        
        // check if stock exists
        if ($stock == false)
        {
            apologize("You must enter a valid symbol");
        }
        
        // format the 'price'
        $stock["price"] = number_format($stock["price"], 4);
        
        // render details
        render("quote_display.php", ["symbol" => $stock["symbol"], "name" => $stock["name"], "price" => $stock["price"]]);
    }

?>