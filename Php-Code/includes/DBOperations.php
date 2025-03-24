<?php
    class DbOPerations
    {
        private $con;
        function __construct()
        {
            require_once dirname(__FILE__) .'/DBConnect.php';
            $db = new DbConnect();
            $this->con = $db->connect();
        }
        function createUser($username, $pass, $email)
        {
            if($this->isUserExist($username, $pass))
            {
                return 0;
            }
            else
            {
                $password = password_hash($pass,PASSWORD_DEFAULT); 
                $stmt = $this->con->prepare("INSERT INTO `users` (`id`, `username`, `password`, `email`) 
                VALUES (Null, ?, ?, ?);"); 
                $stmt->bind_param("sss",$username, $password,$email);
                if( $stmt->execute() )
                    return 1;
                else
                    return 2;
            }
        }
        public function userLogIn($username, $pass)
        {
            $stmt = $this->con->prepare("SELECT password FROM users WHERE username=?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            $stmt->store_result();
    
        if ($stmt->num_rows > 0) {
        $stmt->bind_result($hashed_password);
        $stmt->fetch();
        if (password_verify($pass, $hashed_password)) {
            return true;
        }
    }
    return false;
}

        public function getUserByUsername($username){
			$stmt = $this->con->prepare("SELECT * FROM users WHERE username = ?");
			$stmt->bind_param("s",$username);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
        }
        private function isUserExist($username,$email)
        {
            $stmt = $this->con->prepare("SELECT id FROM users WHERE username=? OR email=?");
            $stmt->bind_param("ss", $username, $email);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0 ;
        }

    }
?>