<?php

    require(__DIR__ . "/../includes/config.php");
    
    // numerically indexed array of places
    $places = [];
    
    // TODO: search database for places matching $_GET["geo"], store in $places
    $params = array_map('trim', explode(",", urldecode($_GET["geo"])));
    
    if (($index = array_search("US", $params)) !== false) {
        unset($params[$index]);
    }
    
    $sql = "SELECT * FROM places WHERE ";
    for ($i = 0, $count = count($params); $i < $count; $i++) {
        if (is_numeric($params[$i])) {
            $sql .= '(postal_code LIKE "' . htmlspecialchars($params[$i], ENT_QUOTES) . '%" OR ' .
                    'country_code LIKE "' . htmlspecialchars($params[$i], ENT_QUOTES) . '%")'; 
        } else {
            $sql .= 
                '(place_name  LIKE "' . htmlspecialchars($params[$i], ENT_QUOTES) . '%" OR ' . 
                 (strlen($params[$i]) <= 2 ? 'admin_code1 LIKE "' . htmlspecialchars($params[$i], ENT_QUOTES) . '%" OR ' : "") . 
                 'admin_name1 LIKE "' . htmlspecialchars($params[$i], ENT_QUOTES) . '%" OR ' .
                 'country_code LIKE "' . htmlspecialchars($params[$i], ENT_QUOTES) . '%")';
        }
        
        if ($i < ($count - 1)) {
            $sql .= " AND ";
        }
    }
    
    $places = CS50::query($sql);
    
    // output places as JSON (pretty-printed for debugging convenience)
    header("Content-type: application/json");
    print(json_encode($places, JSON_PRETTY_PRINT));
    
?>