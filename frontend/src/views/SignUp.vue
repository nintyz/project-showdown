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
                <h1>Create Your Account</h1>

                <!-- Full Name -->
                <div class="form-group">
                    <label for="fullname">Full Name</label>
                    <input type="text" id="fullname" v-model="fullname" placeholder="Enter your full name" />
                </div>

                <!-- Name and Age on the same row -->
                <div class="form-row">
                    <div class="form-group half-width">
                        <label for="name">Player Nickname</label>
                        <input type="text" id="name" v-model="name" placeholder="Enter your player name" />
                    </div>

                    <div class="form-group half-width">
                        <label for="age">Age</label>
                        <input type="number" id="age" v-model="age" placeholder="Enter your age" />
                    </div>
                </div>

                <!-- Birthdate and Country on the same row -->
                <div class="form-row">
                    <div class="form-group half-width">
                        <label for="birthdate">Birthdate</label>
                        <input type="date" id="birthdate" v-model="birthdate" />
                    </div>

                    <div class="form-group half-width">
                        <label for="country">Country</label>
                        <input type="text" id="country" v-model="country" placeholder="Enter your country" />
                    </div>
                </div>

                <!-- Email -->
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" v-model="email" placeholder="Enter your email" />
                </div>

                <!-- Password -->
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" v-model="password" placeholder="Create a password" />
                </div>

                <!-- Ranking and ELO on the same row -->
                <div class="form-row">
                    <div class="form-group half-width">
                        <label for="ranking">Ranking</label>
                        <input type="number" id="ranking" v-model="ranking" placeholder="Enter your ranking" />
                    </div>

                    <div class="form-group half-width">
                        <label for="elo">ELO</label>
                        <input type="number" id="elo" v-model="elo" placeholder="Enter your ELO" />
                    </div>
                </div>

                <!-- Sign-Up Button -->
                <button @click="handleSignUp" class="sign-up-btn">Sign Up</button>
            </div>
        </div>
    </div>
</template>

<script>
import '@/assets/main.css'; 
export default {
    data() {
        return {
            fullname: "",
            name: "",
            age: null,
            birthdate: "",
            country: "",
            email: "",
            password: "",
            ranking: null,
            elo: null,
        };
    },
    methods: {
        handleSignUp() {
            console.log("Player Info:", {
                fullname: this.fullname,
                name: this.name,
                age: this.age,
                birthdate: this.birthdate,
                country: this.country,
                email: this.email,
                password: this.password,
                ranking: this.ranking,
                elo: this.elo,
            });
        },
    },
};
</script>

<style scoped>
.signup-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #f3eeea;
    height: 100vh;
    justify-content: center;
    position: relative;
}

.logo {
    width: 250px;
    height: auto;
    margin-bottom: 1.5em;
}

.signup-box-wrapper {
    display: flex;
    background-color: white;
    width: 100%;
    height: 650px;
    max-width: 1000px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    overflow: hidden;
    position: relative;
}

.signup-box {
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

.form-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
}

.half-width {
    width: 48%;
}

label {
    display: block;
    text-align: left;
    font-weight: bold;
    margin-bottom: 3px;
    font-size: 14px;
    color: #776b5d;
}

input {
    width: 100%;
    padding: 8px;
    border: 1px solid #b0a695;
    border-radius: 5px;
    background-color: #f3eeea;
    color: #776b5d;
}

input::placeholder {
    color: #b0a695;
    padding: 10px;
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

.left-side {
    width: 50%;
}

.full-size-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>