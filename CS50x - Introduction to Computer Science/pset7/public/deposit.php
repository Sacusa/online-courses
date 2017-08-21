<?php

    // configuration
    require("../includes/config.php"); 
    
    // if user reached page via GET (as by clicking a link or via redirect)
    if ($_SERVER["REQUEST_METHOD"] == "GET")
    {
        // render form
        render("deposit_input.php", ["title" => "Deposit"]);
    }
    
    // else if user reached page via POST (as by submitting a form via POST)
    else if ($_SERVER["REQUEST_METHOD"] == "POST")
    {
        // validate submission
        if (empty($_POST["deposit"]))
        {
            apologize("You must enter the deposit amount");
        }
        else if (!preg_match("/^\d+$/", $_POST["deposit"]))
        {
            apologize("Invalid deposit amount");
        }
        
        // get the value of cash
        $cash = CS50::query("SELECT cash FROM users WHERE id = ?", $_SESSION["id"]);
        $cash = $cash[0]['cash'];
        
        // update cash
        $cash = $cash + $_POST["deposit"];
        CS50::query("UPDATE users SET cash = ? WHERE id = ?", $cash, $_SESSION["id"]);
        
        redirect("/");
    }
    
?>