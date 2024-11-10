<template>
    <div class="signup-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />

        <div class="signup-box-wrapper">
            <div class="left-side">
                <img src="@/assets/signup-photo.jpg" alt="Illustration" class="full-size-img" />
            </div>

            <div class="signup-box">
                <h1>Verify Email and Sign Up</h1>

                <!-- Sign Up Form -->
                <div v-if="!isVerificationStep">
                    <!-- Email Input -->
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" v-model="email" placeholder="Enter your email" />
                    </div>

                    <!-- Password Input with Strength Indicator -->
                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="password-input-container">
                            <input :type="showPassword ? 'text' : 'password'" id="password" v-model="password"
                                placeholder="Create a password" />
                            <button type="button" @click="togglePasswordVisibility">
                                {{ showPassword ? 'Hide' : 'Show' }}
                            </button>
                        </div>
                        <div class="password-strength-bar">
                            <span v-for="(block, index) in 5" :key="index"
                                :class="{ 'strength-item': true, valid: index < requirementsMetCount }"></span>
                        </div>
                        <p class="password-requirements">
                            Min. 8 characters, 1 lowercase, 1 uppercase, 1 number. Allowed special characters: !@#$%^
                        </p>
                    </div>

                    <!-- Role Dropdown -->
                    <div class="form-group">
                        <label for="role">Role</label>
                        <select id="role" v-model="role">
                            <option value="" disabled>Select your role</option>
                            <option value="player">Player</option>
                            <option value="organiser">Organiser</option>
                        </select>
                    </div>

                    <!-- Sign Up Button -->
                    <button @click="handleSignUp" class="sign-up-btn" :disabled="!isPasswordValid">
                        Verify Email and Sign Up
                    </button>
                </div>

                <!-- Display Success/Error Messages -->
                <p v-if="message" :class="{ success: success, error: !success }">{{ message }}</p>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            email: '',
            password: '',
            showPassword: false,
            role: '',
            message: '',
            success: false,
            isVerificationStep: false
        };
    },
    computed: {
        hasUpperCase() {
            return /[A-Z]/.test(this.password);
        },
        hasLowerCase() {
            return /[a-z]/.test(this.password);
        },
        hasNumber() {
            return /[0-9]/.test(this.password);
        },
        hasSpecialChar() {
            return /[!@#$%^&*]/.test(this.password);
        },
        isMinLength() {
            return this.password.length >= 8;
        },
        requirementsMetCount() {
            return [this.isMinLength, this.hasLowerCase, this.hasUpperCase, this.hasNumber, this.hasSpecialChar].filter(Boolean).length;
        },
        isPasswordValid() {
            return this.requirementsMetCount === 5;
        },
    },
    methods: {
        async handleSignUp() {
            if (!this.email || !this.password || !this.role) {
                this.message = "Please fill out all fields.";
                this.success = false;
                return;
            }

            if (!this.isPasswordValid) {
                this.message = "Password does not meet the requirements.";
                this.success = false;
                return;
            }

            try {
                // Call the backend to create the user and initiate email verification
                const response = await axios.post('http://localhost:8080/users', {
                    email: this.email,
                    password: this.password,
                    role: this.role,
                });
                localStorage.setItem("role", this.role);
                this.success = true;

                console.log(response);

                await new Promise(resolve => setTimeout(resolve, 1000));

                // Call send-verification-email after successful user creation
                const sendEmail = await axios.post('http://localhost:8080/send-verification-email', this.email, {
                    headers: {
                        'Content-Type': 'text/plain'
                    }
                });
                this.message = "Verification code sent to your email.";
                this.isVerificationStep = true;
                console.log(sendEmail);

                // Redirect to the /verify page with the email as a query parameter
                this.$router.push({ path: '/verify', query: { email: this.email, source: 'signup' } });

            } catch (error) {
                this.message = error.response?.data || "Sign-up failed. Please try again.";
                this.success = false;
            }
        },

        togglePasswordVisibility() {
            this.showPassword = !this.showPassword;
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
    height: 660px;
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

.password-input-container {
    display: flex;
    align-items: center;
}

.password-input-container input {
    flex: 1;
}

.password-input-container button {
    background: none;
    border: none;
    color: #776b5d;
    cursor: pointer;
    padding: 0 10px;
    width: 15%;
}

input,
select {
    width: 100%;
    padding: 8px;
    border: 1px solid #b0a695;
    border-radius: 5px;
    background-color: #f3eeea;
}

.password-strength-bar {
    display: flex;
    margin-top: 5px;
}

.strength-item {
    height: 5px;
    width: 20%;
    background-color: #ccc;
    margin-right: 2px;
    border-radius: 3px;
    transition: background-color 0.3s ease;
}

.strength-item.valid {
    background-color: green;
}

.password-requirements {
    font-size: 12px;
    color: #d9534f;
    margin-top: 5px;
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

.sign-up-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.left-side {
    width: 50%;
}

.full-size-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.success {
    color: green;
    font-size: 14px;
    margin-top: 10px;
    text-align: center;
}

.error {
    color: red;
    font-size: 14px;
    margin-top: 10px;
    text-align: center;
}
</style>
