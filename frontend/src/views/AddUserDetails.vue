<template>
  <div class="user-details-container">
    <img src="@/assets/logo.png" alt="Logo" class="logo" />

    <div class="details-box-wrapper">
      <h1>Complete Your Profile</h1>

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
          <input type="date" id="dob" v-model="dob" />
        </div>

        <!-- Age -->
        <div class="form-group">
          <label for="age">Age</label>
          <input type="number" id="age" v-model="age" placeholder="Enter your age" />
        </div>

        <!-- Ranking and ELO -->
        <div class="form-group">
          <label for="rank">Ranking</label>
          <input type="number" id="rank" v-model="rank" placeholder="Enter your ranking" />
        </div>

        <div class="form-group">
          <label for="elo">ELO</label>
          <input type="number" id="elo" v-model="elo" placeholder="Enter your ELO" />
        </div>

        <!-- Raw Scores for Clay, Grass, and Hard Surfaces -->
        <div class="form-group">
          <label for="clayRaw">Clay Raw</label>
          <input type="number" id="clayRaw" v-model="clayRaw" placeholder="Enter Clay Raw score" />
        </div>

        <div class="form-group">
          <label for="grassRaw">Grass Raw</label>
          <input type="number" id="grassRaw" v-model="grassRaw" placeholder="Enter Grass Raw score" />
        </div>

        <div class="form-group">
          <label for="hardRaw">Hard Raw</label>
          <input type="number" id="hardRaw" v-model="hardRaw" placeholder="Enter Hard Raw score" />
        </div>

        <!-- Peak Age and Peak ELO -->
        <div class="form-group">
          <label for="peakAge">Peak Age</label>
          <input type="number" id="peakAge" v-model="peakAge" placeholder="Enter Peak Age" />
        </div>

        <div class="form-group">
          <label for="peakElo">Peak ELO</label>
          <input type="number" id="peakElo" v-model="peakElo" placeholder="Enter Peak ELO" />
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
  data() {
    return {
      fullname: '',
      dob: '',
      age: null,
      country: '',
      rank: null,
      elo: null,
      clayRaw: null,
      grassRaw: null,
      hardRaw: null,
      peakAge: null,
      peakElo: null,
      achievements: '',
      bio: '',
      userId: null,
    };
  },
  methods: {
    async handleSave() {
      this.userId = localStorage.getItem('userId');

      try {
        const userDetails = {
          email: this.email,
          password: this.password,
          role: this.role,
          playerDetails: {
            achievements: this.achievements,
            age: this.age,
            bio: this.bio,
            clayRaw: this.clayRaw,
            country: this.country,
            dob: this.dob,
            elo: this.elo,
            grassRaw: this.grassRaw,
            hardRaw: this.hardRaw,
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
}

.logo {
  width: 250px;
  margin-bottom: 1.5em;
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
</style>
