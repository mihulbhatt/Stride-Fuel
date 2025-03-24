<?php
header("Content-Type: application/json");

// Check if required POST parameters are provided
if (empty($_POST['query']) || empty($_POST['userID'])) {
    echo json_encode(["error" => "No query and/or userID provided"]);
    exit;
}

$query = $_POST['query'];
$userId = $_POST['userID'];


// NutritionX API endpoint and credentials
$apiUrl = "https://trackapi.nutritionix.com/v2/natural/nutrients";
$appId = "45cd790a"; // Application ID
$appKey = "d093763f13558e6e7bb5160a6e88667b"; // Application Key

// Build the request payload
$data = json_encode(["query" => $query]);

// Initialize cURL session to call NutritionX API
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $apiUrl);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Content-Type: application/json",
    "x-app-id: $appId",
    "x-app-key: $appKey"
]);

$response = curl_exec($ch);
if (curl_errno($ch)) {
    echo json_encode(["error" => curl_error($ch)]);
    curl_close($ch);
    exit;
}
curl_close($ch);

// Decode the API response
$jsonData = json_decode($response, true);

// Including database connection class and get a connection
include_once '../includes/DBConnect.php';
$db = new DbConnect();
$mysqli = $db->connect();

// Checking if the API response contains foods data
if (isset($jsonData['foods']) && is_array($jsonData['foods'])) {
    foreach ($jsonData['foods'] as $food) {
        // Extract and sanitize nutritional details
        $foodName = $mysqli->real_escape_string($food['food_name']);
        $calories = isset($food['nf_calories']) ? $food['nf_calories'] : 0;
        $protein = isset($food['nf_protein']) ? $food['nf_protein'] : 0;
        $carbs = isset($food['nf_total_carbohydrate']) ? $food['nf_total_carbohydrate'] : 0;
        $fats = isset($food['nf_total_fat']) ? $food['nf_total_fat'] : 0;

        // Optionally extract the image URL from the API response without storing it in your DB
        $imageUrl = "";
        if (isset($food['photo']) && isset($food['photo']['thumb'])) {
            $imageUrl = $food['photo']['thumb'];
        }

        if (!empty($_POST['meal_type'])) {
            $meal_type = $_POST['meal_type'];
            $sql1 = "INSERT INTO user_meals (user_id, food_name, calories, protein, carbs, fats, meal_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
            $stmt1 = $mysqli->prepare($sql1);
            if ($stmt1) {
                // Bind parameters: "i" for integer, "s" for string, "d" for double/decimal
                $stmt1->bind_param("isdddds", $userId, $foodName, $calories, $protein, $carbs, $fats, $meal_type);
                $stmt1->execute();
                $stmt1->close();
            } else {
                echo json_encode(["error" => "Failed to prepare statement: " . $mysqli->error]);
                $mysqli->close();
                exit;
            }
        } else {
            // Prepare the SQL query to insert the meal data (without the image URL)
            $sql = "INSERT INTO user_meals (user_id, food_name, calories, protein, carbs, fats) VALUES (?, ?, ?, ?, ?, ?)";
            $stmt = $mysqli->prepare($sql);
            if ($stmt) {
                // Bind parameters: "i" for integer, "s" for string, "d" for double/decimal
                $stmt->bind_param("isdddd", $userId, $foodName, $calories, $protein, $carbs, $fats);
                $stmt->execute();
                $stmt->close();
            } else {
                echo json_encode(["error" => "Failed to prepare statement: " . $mysqli->error]);
                $mysqli->close();
                exit;
            }
        }
    }
}


// Close the database connection
$mysqli->close();

// Return the original API response as JSON (which includes the image URL)
echo $response;
?>