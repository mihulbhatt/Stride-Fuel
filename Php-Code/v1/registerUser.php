<?php
    require_once '../includes/DBOperations.php';
    $response = array();
    if($_SERVER['REQUEST_METHOD'] == 'POST') 
    {
        if(isset($_POST['username']) //User has provided all the values
        && isset($_POST['email']) 
        && isset($_POST['password']))
        {   
            $db = new DBOperations();
            $result = $db->createUser($_POST['username'],
                                      $_POST['password'],
                                      $_POST['email']);
            if($result == 1)
            {
                $response['error'] = false;
                $response['message'] = "User registered successfully";
            }
            elseif($result == 2)
            {
                $response["error"] = true;
                $response["message"] = "Some error occured, please try again";
            }
            elseif($result == 0)
            {
                $response["error"] = true;
                $response["message"] = "Username or Email is already registered, Log in or Use different username or email";
            }
        }
        else
        {
            $response['error'] = true;
            $response['message'] = "Required fields are missing";
        }
    }
    else 
    {
        $response['error'] = true;
        $response['message'] = "Invalid request";
    }
    echo json_encode($response);
?>