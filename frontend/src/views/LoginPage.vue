<template>
    <div class="login-container">
      <img src="@/assets/logo.png" alt="Logo" class="logo" />
  
      <div class="login-box-wrapper">
  
        <div class="login-box">
          <h1>Welcome Back!</h1>
  
          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" v-model="email" placeholder="Enter your email" />
          </div>
  
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="password" placeholder="Enter your password" />
          </div>
  
          <!-- Sign In Button -->
          <button @click="handleSignIn" class="sign-in-btn">Next</button>
  
          <!-- OR Divider -->
          <div class="divider">OR</div>
  
          <!-- Google & Facebook Login Buttons -->
          <button class="google-btn" @click="handleGoogleSignIn">
            <img src="@/assets/google-icon.png" />
            Continue with Google
          </button>
          <button class="facebook-btn" @click="handleFacebookSignIn">
            <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/F_icon.svg/512px-F_icon.svg.png" />
            Continue with Facebook
          </button>
  
          <!-- No account? Sign up here Link -->
          <div class="no-account">
            <p>No account?
              <a @click="goToSignUp" class="signup-link">Sign up here</a>
            </p>
          </div>
        </div>
  
        <div class="right-side">
          <img src="@/assets/login-photo.jpg" alt="Illustration" class="full-size-img" />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        email: "",
        password: ""
      };
    },
    methods: {
      handleSignIn() {
        // Admin login logic
        if (this.email === "admin@gmail.com") {
          localStorage.setItem("role", "admin");
          this.$router.push("/admin-dashboard");
        } 
        // Player login logic
        else if (this.email === "player@gmail.com") {
          localStorage.setItem("role", "player");
          this.$router.push("/dashboard");
        } else {
          console.log("Invalid email");
          // Handle other login cases (show error message etc.)
        }
      },
      handleGoogleSignIn() {
        console.log("Google Sign-In");
        // Add logic for Google sign-in
      },
      handleFacebookSignIn() {
        console.log("Facebook Sign-In");
        // Add logic for Facebook sign-in
      },
      goToSignUp() {
        // Redirect to sign-up page
        this.$router.push('/signup');
      }
    }
  };
  </script>
  
  <style scoped>
  .divider {
    text-align: center;
    margin: 20px 0;
  }
  
  .google-btn, .facebook-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 10px;
    margin: 10px 0;
    border: 1px solid #ccc;
    border-radius: 5px;
    cursor: pointer;
    background-color: white;
  }
  
  .google-btn img, .facebook-btn img {
    width: 20px;
    margin-right: 10px;
  }
  
  .no-account {
    margin-top: 15px;
    text-align: center;
  }
  
  .signup-link {
    color: #776b5d;
    text-decoration: underline;
    cursor: pointer;
  }
  
  .signup-link:hover {
    color: #b0a695;
  }
  
  .login-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #f3eeea; 
    height: 100vh;
    justify-content: center;
  }
  
  .logo {
    width: 250px; 
    margin-bottom: 1.5em; 
  }
  
  .login-box-wrapper {
    display: flex;
    background-color: white;
    width: 100%;
    height: 550px;
    max-width: 1000px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    overflow: hidden;
  }
  
  .login-box {
    background-color: #ebe3d5; 
    padding: 30px; 
    width: 50%;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  h1 {
    font-size: 20px; 
    color: #b0a695; 
    margin-bottom: 15px;
  }
  
  .form-group {
    margin-bottom: 10px; 
  }
  
  label {
    display: block;
    text-align: left;
    font-weight: bold;
    margin-bottom: 5px;
    color: #776b5d; 
  }
  
  input {
    width: 100%;
    padding: 8px;
    border: 1px solid #b0a695;
    border-radius: 5px;
    background-color: #f3eeea; 
  }
  
  input::placeholder {
    color: #b0a695; 
  }
  
  .sign-in-btn {
    width: 100%;
    padding: 10px;
    background-color: #776b5d; 
    border: none;
    border-radius: 5px;
    color: white;
    cursor: pointer;
    margin-top: 15px; 
  }
  
  .sign-in-btn:hover {
    background-color: #b0a695; 
  }
  
  .right-side {
    width: 50%; 
  }
  
  .full-size-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  </style>
  