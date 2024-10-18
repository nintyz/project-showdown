<template>
    <div class="signup-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />

        <div class="signup-box-wrapper">
            <!-- Left side with an image -->
            <div class="left-side">
                <img src="@/assets/signup-photo.jpg" alt="Illustration" class="full-size-img" />
            </div>

            <!-- Right side form for signup -->
            <div class="signup-box">
                <h1>Sign Up</h1>

                <!-- Email and Password -->
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" v-model="email" placeholder="Enter your email" />
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" v-model="password" placeholder="Create a password" />
                </div>

                <!-- Sign Up Button -->
                <button @click="handleSignUp" class="sign-up-btn">Sign Up</button>

                <!-- OR Divider -->
                <div class="divider">OR</div>

                <!-- Google & Facebook Sign Up Buttons -->
                <button class="google-btn" @click="handleGoogleSignUp">
                    <img src="@/assets/google-icon.png" />
                    Continue with Google
                </button>
                <button class="facebook-btn" @click="handleFacebookSignUp">
                    <img
                        src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/F_icon.svg/512px-F_icon.svg.png" />
                    Continue with Facebook
                </button>

                <!-- Already have an account? Link -->
                <div class="account-exists">
                    <p>Already have an account?
                        <a @click="goToLogin" class="login-link">Login here</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    data() {
        return {
            email: '',
            password: ''
        };
    },
    methods: {
        handleSignUp() {
            // Perform email/password sign-up
            localStorage.setItem("role", "player");
            // After successful sign-up, redirect to the next page to collect more information
            this.$router.push('/user-details');
            
        },
        handleGoogleSignUp() {
            // Perform Google sign-up
            this.$router.push('/user-details');
        },
        handleFacebookSignUp() {
            // Perform Facebook sign-up
            this.$router.push('/user-details');
        },
        goToLogin() {
            // Redirect to login page
            this.$router.push('/login');
        }
    }
};
</script>


<style scoped>
.account-exists {
    margin-top: 15px;
    text-align: center;
}

.login-link {
    color: #776b5d;
    text-decoration: underline;
    cursor: pointer;
}

.login-link:hover {
    color: #b0a695;
}

.signup-container {
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

.signup-box-wrapper {
    display: flex;
    background-color: white;
    width: 100%;
    max-width: 1000px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    overflow: hidden;
    height: 550px;
}

.signup-box {
    background-color: #ebe3d5;
    padding: 30px;
    width: 50%;
    display: flex;
    flex-direction: column;
}

h1 {
    font-size: 20px;
    color: #b0a695;
    margin-bottom: 15px;
}

.form-group {
    margin-bottom: 10px;
}

input {
    width: 100%;
    padding: 8px;
    border: 1px solid #b0a695;
    border-radius: 5px;
    background-color: #f3eeea;
}

.sign-up-btn {
    width: 100%;
    padding: 10px;
    background-color: #776b5d;
    border: none;
    border-radius: 5px;
    color: white;
    cursor: pointer;
    margin-top: 15px;
}

.sign-up-btn:hover {
    background-color: #b0a695;
}

.divider {
    text-align: center;
    margin: 20px 0;
}

.google-btn,
.facebook-btn {
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

.google-btn img,
.facebook-btn img {
    width: 20px;
    margin-right: 10px;
}

.left-side {
    width: 50%;
}

.full-size-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>