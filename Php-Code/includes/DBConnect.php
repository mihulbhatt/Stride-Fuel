<?php
    class DbConnect
    {
        private $con;
        
        public function connect()
        {
            include_once dirname(__FILE__) .'/Constants.php';
            $this->con = new mysqli(DB_HOST,DB_USER,DB_PASSWORD, DB_NAME);
            if (mysqli_connect_errno())
            {
                echo"Failed to connect with database". mysqli_connect_error();
            }
            return $this->con;
        }
    }
?>