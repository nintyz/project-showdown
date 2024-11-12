<template>
    <div class="player-details">
        <div class="form-group">
            <label>
                <input type="checkbox" v-model="isNewPlayer" /> I am a new player (I have not participated in any
                tournaments)
            </label>
        </div>

        <div class="form-grid">
            <div class="form-group">
                <label for="fullname">Full Name</label>
                <input type="text" id="fullname" v-model="localPlayerDetails.fullname"
                    placeholder="Enter your full name" />
            </div>
            <div class="form-group">
                <label for="country">Country</label>
                <input type="text" id="country" v-model="localPlayerDetails.country" placeholder="Enter your country" />
            </div>
            <div class="form-group">
                <label for="dob">Date of Birth</label>
                <input type="date" id="dob" v-model="localPlayerDetails.dob" @change="calculatePeakAge" />
            </div>
            <div class="form-group">
                <label for="rank">Ranking</label>
                <input type="number" id="rank" v-model="localPlayerDetails.rank" placeholder="Enter your ranking"
                    :disabled="isNewPlayer" />
            </div>
            <div class="form-group">
                <label for="elo">ELO</label>
                <input type="number" id="elo" v-model="localPlayerDetails.elo" placeholder="Enter your ELO"
                    :disabled="isNewPlayer" />
            </div>
            <div class="form-group">
                <label for="peakAge">Peak Age</label>
                <input type="number" id="peakAge" v-model="localPlayerDetails.peakAge" placeholder="Enter Peak Age"
                    :disabled="isNewPlayer" />
            </div>
            <div class="form-group">
                <label for="peakElo">Peak ELO</label>
                <input type="number" id="peakElo" v-model="localPlayerDetails.peakElo" placeholder="Enter Peak ELO"
                    :disabled="isNewPlayer" />
            </div>
            <div class="form-group full-width">
                <label for="achievements">Achievements (Optional)</label>
                <textarea id="achievements" v-model="localPlayerDetails.achievements"
                    placeholder="Enter your achievements"></textarea>
            </div>
            <div class="form-group full-width">
                <label for="bio">Bio</label>
                <textarea id="bio" v-model="localPlayerDetails.bio" placeholder="Enter a brief bio"></textarea>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: ['playerDetails'],
    data() {
        return {
            localPlayerDetails: { ...this.playerDetails },
            isNewPlayer: false,
        };
    },
    watch: {
        isNewPlayer(newVal) {
            if (newVal) {
                this.localPlayerDetails.elo = 2000;
                this.localPlayerDetails.peakElo = 2000;
                this.localPlayerDetails.rank = 0;
                this.calculatePeakAge();
            } else {
                this.localPlayerDetails.elo = null;
                this.localPlayerDetails.peakElo = null;
                this.localPlayerDetails.rank = null;
                this.localPlayerDetails.peakAge = null;
            }
        }
    },
    methods: {
        calculatePeakAge() {
            if (this.localPlayerDetails.dob) {
                const birthYear = new Date(this.localPlayerDetails.dob).getFullYear();
                this.localPlayerDetails.peakAge = new Date().getFullYear() - birthYear;
            }
        },
        getFormData() {
            return this.localPlayerDetails;
        }
    },
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