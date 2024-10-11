<template>
    <div class="login-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />

        <div class="login-box-wrapper">

            <div class="login-box">
                <h1>Welcome Back!</h1>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" v-model="email" placeholder="Enter your email" />
                    <!-- <a href="#" class="forgot-link">Forgot email?</a> -->
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" v-model="password" placeholder="Enter your password" />
                    <!-- <a href="#" class="forgot-link">Forgot password?</a> -->
                </div>

                <button @click="handleSignIn" class="sign-in-btn">Next</button>

                <a href="#" class="create-account">Create Account</a>
            </div>

            <div class="right-side">
                <img src="@/assets/login-photo.jpg" alt="Illustration" class="full-size-img" />
            </div>
        </div>
    </div>
</template>

<script>
import '@/assets/main.css'; 
export default {
    data() {
        return {
            email: "",
            password: ""
        };
    },
    methods: {
        handleSignIn() {

            console.log("Email:", this.email);
            console.log("Password:", this.password);
        }
    }
};
</script>

<style scoped>
.login-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #F3EEEA;
    height: 100vh;
    justify-content: center;
    position: relative;
}


.logo {
    width: 300px;
    height: auto;
    margin-bottom: 2em;
}


.login-box-wrapper {
    display: flex;
    background-color: white;
    width: 100%;
    height: 500px;
    max-width: 1000px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    overflow: hidden;
    position: relative;
    z-index: 0;
}


.login-box {
    background-color: #EBE3D5;
    padding: 40px;
    width: 50%;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

h2 {
    font-size: 18px;
    color: #B0A695;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    text-align: left;
    font-weight: bold;
    margin-bottom: 5px;
    color: #776B5D;
}

input {
    width: 100%;
    padding: 10px 0px 10px 0px;
    border: 1px solid #B0A695;
    border-radius: 5px;
    background-color: #F3EEEA;
    color: #776B5D;
    margin-bottom: 10px;
}

input::placeholder {
    color: #B0A695;
    padding: 10px;
}

.forgot-link {
    display: block;
    text-align: right;
    font-size: 12px;
    color: #B0A695;
    margin-bottom: 20px;
}

.sign-in-btn {
    width: 100%;
    padding: 10px;
    background-color: #776B5D;
    border: none;
    border-radius: 5px;
    color: white;
    cursor: pointer;
}

.sign-in-btn:hover {
    background-color: #B0A695;
}

.create-account {
    display: block;
    margin-top: 10px;
    color: #776B5D;
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