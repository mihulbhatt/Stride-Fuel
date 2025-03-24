# Stride Fuel

Stride Fuel is a comprehensive nutrition tracking app that helps users monitor their daily calorie intake, manage their macro progress, and log their meals. Built using XML, Java, PHP, and MySQL, the app offers an intuitive interface and powerful tracking features to support users in achieving their nutrition goals.

## Demo

Click below to watch the demo.

[![Demo Video](./git-images/StrideFuelThumbnail.png)](https://drive.google.com/file/d/1TGMAvYfLBKj7W09JjOYt8Gj1EYT4An3v/view?usp=sharing)


## Features

### Calorie Tracking
- Displays the total calories the user needs to consume.
- Shows the calories remaining and the calories already consumed.

### Macros Monitoring
- Tracks the progress of macronutrients (proteins, carbohydrates, fats) throughout the day.

### Meals Section
- Users can add and manage meals for breakfast, lunch, dinner, and snacks.

### Track Fragment
- Offers a detailed view of the calories and macros for each entered meal.

## Technologies Used
- **XML:** Used for designing user interfaces and layouts.
- **Java:** Core language for app functionality and logic.
- **PHP:** Server-side scripting for backend operations.
- **MySQL:** Database management to store user data, meals, and nutritional information.

## Installation & Setup

### Backend Setup (PHP & MySQL)
1. **Database:**  
   - Create a MySQL database (e.g., `stride_fuel_db`) and import the provided SQL schema.
2. **PHP Configuration:**  
   - Update the database credentials in your PHP configuration files (e.g., `db.php`).
3. **Deployment:**  
   - Deploy the PHP files on a web server (e.g., Apache) with PHP support.

### Android App Setup (XML & Java)
1. **Project Setup:**  
   - Open the project in Android Studio.
2. **Build & Run:**  
   - Sync your project with Gradle, build the project, and run it on an emulator or physical device.

## Usage

- **Calorie & Macros Overview:**  
  Launch the app to view your daily nutritional goals along with the calories and macros consumed.
- **Adding Meals:**  
  Navigate to the meals section to add your meals for breakfast, lunch, dinner, and snacks.
- **Detailed Tracking:**  
  Use the Track Fragment to review detailed information about the calories and macros of each logged meal.

## Credits

- **NutritionX API:**  
  Stride Fuel utilizes the NutritionX API to fetch nutritional information based on the user's input food. We thank NutritionX for providing this valuable service.

## Contributing

Contributions to Stride Fuel are welcome! If you'd like to contribute:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and test thoroughly.
4. Submit a pull request with a detailed description of your changes.
