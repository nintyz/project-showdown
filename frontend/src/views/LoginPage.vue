<template>
    <div class="login-container">
      <img src="@/assets/logo.png" alt="Logo" class="logo" />
  
      <div class="login-box-wrapper" :class="{ 'has-error': error }">
  
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

          <!-- Error message -->
          <transition name="fade-slide">
            <div v-if="error" class="error-message">
              {{ error }}
            </div>
          </transition>

          <!-- Sign In Button -->
          <button @click="handleSignIn" class="sign-in-btn" :disabled="isLoading">
            {{ isLoading ? 'Signing in...' : 'Next' }}
          </button>
  
          <!-- OR Divider -->
          <div class="divider">OR</div>
  
          <!-- Google & Facebook Login Buttons -->
          <button class="google-btn" @click="handleGoogleSignIn">
            <img src="@/assets/google-icon.png"  alt="google icon"/>
            Continue with Google
          </button>
          <button class="facebook-btn" @click="handleFacebookSignIn">
            <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/F_icon.svg/512px-F_icon.svg.png"  alt="facebook icon"/>
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
  import axios from 'axios';
  export default {
    data() {
      return {
        email: "",
        password: "",
        error: null,
        isLoading: false
      };
    },
    methods: {
      async handleSignIn() {
        this.isLoading = true;
        this.error = null;

        try {
          const response = await axios.post('http://localhost:8080/login', {
            email: this.email,
            password: this.password
          });

          if (response.data.requiresTwoFactor) {
            this.$router.push(`/verify-2fa?email=${this.email}`);
          } else {
            const token = response.data.token;
            localStorage.setItem('token', token);

            const userId = response.data.userId;
            localStorage.setItem('userId', userId);

            // Extract role from JWT
            const role = this.extractRoleFromToken(token);
            localStorage.setItem('role', role);

            // Redirect based on role
            // if (role === 'admin') {
              this.$router.push('/all-tournaments-dashboard');
            // } else {
            //   this.$router.push('/dashboard');
            // }
          }
        } catch (error) {
          console.error('Login error:', error);
          if (error.response?.data === "Account not verified. Please verify your account.") {
            await axios.post(`http://localhost:8080/resend?email=${this.email}`);
            this.$router.push(`/verify?email=${this.email}`);
          } else {
            localStorage.setItem('role', 'guest');
            this.error = error.response?.data || 'Login failed. Please try again.';
          }
        } finally {
          this.isLoading = false;
        }
      },
      extractRoleFromToken(token) {
        try {
          const base64Url = token.split('.')[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const payload = JSON.parse(window.atob(base64));
          return payload.role || 'player'; // Default to PLAYER if no role found
        } catch (error) {
          console.error('Error extracting role from token:', error);
          return 'player';
        }
      },
      handleGoogleSignIn() {
        console.log("Google Sign-In");
        // Add logic for Google sign-in
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
      },
      handleFacebookSignIn() {
        console.log("Facebook Sign-In");
        // Add logic for Facebook sign-in
        window.location.href = 'http://localhost:8080/oauth2/authorization/facebook';
      },
      goToSignUp() {
        // Redirect to sign-up page
        this.$router.push('/signup');
      }
    }
  };
  </script>
  
  <style scoped>
  .error-message {
    color: #dc3545;
    background-color: #f8d7da;
    padding: 0.75rem;
    border-radius: 5px;
    width: 100%;
    text-align: center;
    font-size: 0.875rem;
  }

  .fade-slide-enter-active,
  .fade-slide-leave-active {
    transition: all 0.3s ease;
    max-height: 100px;
  }

  .fade-slide-enter-from,
  .fade-slide-leave-to {
    opacity: 0;
    max-height: 0;
    transform: translateY(0px);
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-group input {
    margin-top: 0.25rem;
  }

  .divider {
    text-align: center;
    margin: 10px 0;
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
    transition: height 0.3s ease; /* Add smooth transition */
  }

  /* Add this class for when error exists */
  .login-box-wrapper.has-error {
    height: 600px;
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
  