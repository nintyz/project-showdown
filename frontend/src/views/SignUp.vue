<template>
    <div class="signup-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />

        <div class="signup-box-wrapper">
            <div class="left-side">
                <img src="@/assets/signup-photo.jpg" alt="Illustration" class="full-size-img" />
            </div>

            <div class="signup-box">
                <h1>Sign Up</h1>

                <!-- Step 1: Role selection and details form -->
                <div v-if="!isVerificationStep">
                    <!-- Role Dropdown -->
                    <div class="form-group">
                        <label for="role">Role</label>
                        <select id="role" v-model="role">
                            <option value="" disabled>Select your role</option>
                            <option value="player">Player</option>
                            <option value="organizer">Organizer</option>
                        </select>
                    </div>
                    <div v-if="role === 'player'">
                        <div class="form-group">
                            <label class="inline-label">
                                <input class="new-player-box" type="checkbox" v-model="isNewPlayer" /> I am a new player
                                (have not participated
                                in any tournaments)
                            </label>
                        </div>


                        <div class="form-grid">
                            <div class="form-group">
                                <label for="playerName">Name</label>
                                <input type="text" id="fullname" v-model="name" placeholder="Enter your name" />
                            </div>
                            <div class="form-group">
                                <label for="country">Country</label>
                                <input type="text" id="country" v-model="playerDetails.country"
                                    placeholder="Enter your country" />
                            </div>
                            <div class="form-group">
                                <label for="dob">Date of Birth</label>
                                <input type="date" id="dob" v-model="playerDetails.dob" @change="calculatePeakAge" />
                            </div>
                            <div class="form-group">
                                <label for="rank">Ranking</label>
                                <input type="number" id="rank" v-model="playerDetails.rank"
                                    placeholder="Enter your ranking" :disabled="isNewPlayer" />
                            </div>
                            <div class="form-group">
                                <label for="elo">ELO</label>
                                <input type="number" id="elo" v-model="playerDetails.elo" placeholder="Enter your ELO"
                                    :disabled="isNewPlayer" />
                            </div>
                            <div class="form-group">
                                <label for="peakAge">Peak Age</label>
                                <input type="number" id="peakAge" v-model="playerDetails.peakAge"
                                    placeholder="Enter Peak Age" :disabled="isNewPlayer" />
                            </div>
                            <div class="form-group">
                                <label for="peakElo">Peak ELO</label>
                                <input type="number" id="peakElo" v-model="playerDetails.peakElo"
                                    placeholder="Enter Peak ELO" :disabled="isNewPlayer" />
                            </div>
                            <div class="form-group full-width">
                                <label for="achievements">Achievements (Optional)</label>
                                <textarea id="achievements" v-model="playerDetails.achievements"
                                    placeholder="Enter your achievements"></textarea>
                            </div>
                            <div class="form-group full-width">
                                <label for="bio">Bio</label>
                                <textarea id="bio" v-model="playerDetails.bio"
                                    placeholder="Enter a brief bio"></textarea>
                            </div>
                        </div>
                    </div>

                    <!-- Conditional Fields for Organizer -->
                    <div v-if="role === 'organizer'">
                        <div class="form-group">
                            <label for="organizerName">Organization Name</label>
                            <input type="text" id="organizerName" v-model="name"
                                placeholder="Enter organization name" />
                        </div>
                        <div class="form-group">
                            <label for="country">Country</label>
                            <input type="text" id="country" v-model="organizerDetails.country"
                                placeholder="Enter your country" />
                        </div>
                        <div class="form-group">
                            <label for="bio">Bio</label>
                            <textarea id="bio" v-model="organizerDetails.bio"
                                placeholder="Enter a brief bio"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="websiteLink">Website Link</label>
                            <input type="url" id="websiteLink" v-model="organizerDetails.websiteLink"
                                placeholder="Enter your website link" />
                        </div>
                    </div>

                    <!-- Next Button to Proceed to Email/Password Signup -->
                    <button @click="goToVerificationStep" class="sign-up-btn" :disabled="!role">
                        Next
                    </button>
                </div>

                <!-- Step 2: Email and password signup fields -->
                <div v-if="isVerificationStep">
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

                    <!-- Sign Up Button -->
                    <button @click="handleSignUp" class="sign-up-btn" :disabled="!isPasswordValid">
                        Sign Up
                    </button>
                </div>

                <!-- Success/Error Messages -->
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
            isNewPlayer: false,
            name: '',
            email: '',
            password: '',
            showPassword: false,
            role: '',
            message: '',
            success: false,
            isVerificationStep: false,
            playerDetails: {
                dob: '',
                peakAge: null,
                rank: null,
                elo: null,
                peakElo: null,
                country: '',
                bio: '',
                achievements: ''
            },
            organizerDetails: {
                name: '',
                verified: false, // Default to false for organizers
                bio: '',
                country: '',
                websiteLink: ''
            }
        };
    },
    created() {
        document.body.style.backgroundColor = "#f3eeea";
    },
    watch: {
        isNewPlayer(newVal) {
            if (newVal) {
                this.playerDetails.elo = 2000;
                this.playerDetails.peakElo = 2000;
                this.playerDetails.rank = 0;
                this.calculatePeakAge();
            } else {
                this.playerDetails.elo = null;
                this.playerDetails.peakElo = null;
                this.playerDetails.rank = null;
                this.playerDetails.peakAge = null;
            }
        }
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
        }
    },
    methods: {
        calculatePeakAge() {
            if (this.playerDetails.dob) {
                const birthYear = new Date(this.playerDetails.dob).getFullYear();
                this.playerDetails.peakAge = new Date().getFullYear() - birthYear;
            }
        },
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

            // Prepare requestData based on role
            const requestData = {
                email: this.email,
                password: this.password,
                role: this.role,
                name: this.name,
                playerDetails: this.role === 'player' ? this.playerDetails : null,
                organizerDetails: this.role === 'organizer' ? { ...this.organizerDetails, verified: false } : null
            };

            try {
                // Call the backend to create the user and initiate email verification
                const response = await axios.post('http://localhost:8080/users', requestData);
                localStorage.setItem("userId", response.data.userId);
                localStorage.setItem("role", this.role);
                this.success = true;
                await new Promise(resolve => setTimeout(resolve, 1000));
                // Send verification email
                await axios.post('http://localhost:8080/send-verification-email', this.email, {
                    headers: { 'Content-Type': 'text/plain' }
                });

                this.message = "Verification code sent to your email.";
                this.isVerificationStep = true;
                this.$router.push({ path: '/verify', query: { email: this.email, source: 'signup' } });

            } catch (error) {
                this.message = error.response?.data || "Sign-up failed. Please try again.";
                this.success = false;
            }
        },
        togglePasswordVisibility() {
            this.showPassword = !this.showPassword;
        },
        goToVerificationStep() {
            this.isVerificationStep = true
        }
    }
};
</script>

<style scoped>
.body,
html {
    background-color: #f3eeea !important;
}

.signup-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #f3eeea;
    justify-content: center;
    padding: 20px;
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
}

.left-side {
    width: 40%;
}

.full-size-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.signup-box {
    background-color: #ebe3d5;
    padding: 30px;
    width: 60%;
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


h1 {
    font-size: 24px;
    color: #333;
    text-align: center;
    margin-bottom: 20px;
}


.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    column-gap: 20px;
    row-gap: 15px;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.full-width {
    grid-column: span 2;
}

label {
    font-weight: bold;
    margin-bottom: 5px;
}

input,
textarea {
    padding: 8px;
    border: 1px solid #b0a695;
    border-radius: 5px;
    background-color: #f3eeea;
}

input[disabled] {
    background-color: #e9ecef;
    cursor: not-allowed;
}

textarea {
    resize: vertical;
    min-height: 60px;
}

.save-btn {
    width: 100%;
    padding: 12px;
    background-color: #776b5d;
    border: none;
    border-radius: 5px;
    color: white;
    font-size: 16px;
    cursor: pointer;
    margin-top: 20px;
}

.save-btn:hover {
    background-color: #b0a695;
}

.form-group label input[type="checkbox"] {
    margin-right: 10px;
}

.inline-label {
    display: inline-flex;
    align-items: center;
}

.inline-label input[type="checkbox"] {
    margin-right: 4px;
    /* Optional: small spacing for readability */
}

.new-player-box {
    width: 10%;
}
</style>
