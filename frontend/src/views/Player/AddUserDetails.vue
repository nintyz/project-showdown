<template>
  <div class="user-details-container">
    <img src="@/assets/logo.png" alt="Logo" class="logo" />

    <div class="details-box-wrapper">
      <h1>Complete Your Profile</h1>

      <!-- Display User ID -->
      <p v-if="userId" class="user-id-display">User ID: {{ userId }}</p>

      <!-- New Player Checkbox -->
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="isNewPlayer" /> I am a new player (I have not participated in any tournaments)
        </label>
      </div>

      <!-- Full Name -->
      <div class="form-group">
        <label for="fullname">Full Name</label>
        <input type="text" id="fullname" v-model="fullname" placeholder="Enter your full name" />
      </div>

      <div class="form-grid">
        <!-- Country -->
        <div class="form-group">
          <label for="country">Country</label>
          <input type="text" id="country" v-model="country" placeholder="Enter your country" />
        </div>

        <!-- Date of Birth -->
        <div class="form-group">
          <label for="dob">Date of Birth</label>
          <input type="date" id="dob" v-model="dob" @change="calculatePeakAge" />
        </div>

        <!-- Ranking and ELO -->
        <div class="form-group">
          <label for="rank">Ranking</label>
          <input type="number" id="rank" v-model="rank" placeholder="Enter your ranking" :disabled="isNewPlayer" />
        </div>

        <div class="form-group">
          <label for="elo">ELO</label>
          <input type="number" id="elo" v-model="elo" placeholder="Enter your ELO" :disabled="isNewPlayer" />
        </div>

        <!-- Peak Age and Peak ELO -->
        <div class="form-group">
          <label for="peakAge">Peak Age</label>
          <input type="number" id="peakAge" v-model="peakAge" placeholder="Enter Peak Age" :disabled="isNewPlayer" />
        </div>

        <div class="form-group">
          <label for="peakElo">Peak ELO</label>
          <input type="number" id="peakElo" v-model="peakElo" placeholder="Enter Peak ELO" :disabled="isNewPlayer" />
        </div>

        <!-- Achievements and Bio (Full-width fields) -->
        <div class="form-group full-width">
          <label for="achievements">Achievements (Optional)</label>
          <textarea id="achievements" v-model="achievements" placeholder="Enter your achievements"></textarea>
        </div>

        <div class="form-group full-width">
          <label for="bio">Bio</label>
          <textarea id="bio" v-model="bio" placeholder="Enter a brief bio"></textarea>
        </div>
      </div>

      <!-- Save Button -->
      <button @click="handleSave" class="save-btn">Save Profile</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  created() {
    this.userId = localStorage.getItem('userId');
    console.log(this.userId);
  },
  data() {
    return {
      fullname: '',
      dob: '',
      country: '',
      rank: null,
      elo: null,
      peakAge: null,
      peakElo: null,
      achievements: '',
      bio: '',
      userId: null, // Store userId from localStorage here
      isNewPlayer: false, // Track if the user is a new player
    };
  },
  watch: {
    isNewPlayer(newVal) {
      // If the user is a new player, set default values
      if (newVal) {
        this.elo = 2000;
        this.peakElo = 2000;
        this.rank = null;
        this.calculatePeakAge();
      } else {
        // Clear fields if they uncheck "new player"
        this.elo = null;
        this.peakElo = null;
        this.rank = null;
        this.peakAge = null;
      }
    }
  },
  computed: {
    formattedDob() {
      // Ensure dob is in YYYY-MM-DD format, if not already
      if (this.dob) {
        const date = new Date(this.dob);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      }
      return '';
    }
  },
  methods: {
    calculatePeakAge() {
      if (this.dob) {
        const birthYear = new Date(this.dob).getFullYear();
        const currentYear = new Date().getFullYear();
        this.peakAge = currentYear - birthYear;
      }
    },
    async handleSave() {
      this.userId = localStorage.getItem('userId');

      try {
        const userDetails = {
          email: this.email,
          password: this.password,
          role: this.role,
          playerDetails: {
            achievements: this.achievements,
            bio: this.bio,
            country: this.country,
            dob: this.formattedDob, // Use formattedDob to ensure YYYY-MM-DD
            elo: this.elo,
            name: this.fullname,
            peakAge: this.peakAge,
            peakElo: this.peakElo,
            rank: this.rank
          }
        };

        const response = await axios.put(`http://localhost:8080/user/${this.userId}`, userDetails);
        console.log("User Details Saved:", response.data);

        // Redirect to the dashboard after saving details
        this.$router.push('/dashboard');
      } catch (error) {
        console.error("Error updating user details:", error);
      }
    }
  }
};
</script>

<style scoped>
.user-details-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f3eeea;
  height: 100vh;
  justify-content: center;
  padding: 20px;
}

.logo {
  width: 150px;
  margin-bottom: 1em;
}

.details-box-wrapper {
  background-color: white;
  padding: 30px;
  max-width: 800px;
  width: 100%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

h1 {
  font-size: 24px;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.user-id-display {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 15px;
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
</style>
