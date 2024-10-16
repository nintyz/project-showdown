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
  
        <!-- Name and Country on the same row -->
        <div class="form-row">
          <div class="form-group half-width">
            <label for="name">Player Nickname</label>
            <input type="text" id="name" v-model="name" placeholder="Enter your player name" />
          </div>
  
          <div class="form-group half-width">
            <label for="country">Country</label>
            <input type="text" id="country" v-model="country" placeholder="Enter your country" />
          </div>
        </div>
  
        <!-- Birthdate -->
        <div class="form-group">
          <label for="birthdate">Birthdate</label>
          <input type="date" id="birthdate" v-model="birthdate" />
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
  
        <!-- Save Button -->
        <button @click="handleSave" class="save-btn">Save Profile</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        fullname: '',
        name: '',
        birthdate: '',
        country: '',
        ranking: null,
        elo: null
      };
    },
    methods: {
      handleSave() {
        // Save the user details
        console.log("User Details Saved:", {
          fullname: this.fullname,
          name: this.name,
          birthdate: this.birthdate,
          country: this.country,
          ranking: this.ranking,
          elo: this.elo
        });
        // Redirect to another page after saving details
        this.$router.push('/dashboard');
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
    max-width: 500px;
    width: 100%;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
  }
  
  h1 {
    font-size: 20px;
    color: #b0a695;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
  }
  
  .half-width {
    width: 48%;
  }
  
  label {
    display: block;
    text-align: left;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  input {
    width: 100%;
    padding: 8px;
    border: 1px solid #b0a695;
    border-radius: 5px;
    background-color: #f3eeea;
  }
  
  .save-btn {
    width: 100%;
    padding: 10px;
    background-color: #776b5d;
    border: none;
    border-radius: 5px;
    color: white;
    cursor: pointer;
    margin-top: 20px;
  }
  
  .save-btn:hover {
    background-color: #b0a695;
  }
  </style>
  