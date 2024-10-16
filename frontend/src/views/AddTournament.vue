<template>
    <!-- Navbar -->
    <Navbar />
    <div class="create-tournament container mt-3">
        <!-- Form Header -->
        <h2>Create New Tournament</h2>

        <!-- Tournament Creation Form -->
        <form @submit.prevent="submitForm" class="form">
            <!-- Tournament Name -->
            <div class="mb-3">
                <label for="tournamentName" class="form-label">Tournament Name</label>
                <input type="text" id="tournamentName" v-model="tournament.name" class="form-control"
                    placeholder="Enter tournament name" required />
            </div>

            <!-- Tournament Logo -->
            <div class="mb-3">
                <label for="tournamentLogo" class="form-label">Tournament Logo</label>
                <input type="file" id="tournamentLogo" @change="onFileChange" class="form-control" accept="image/*"
                    required />
                <div v-if="tournament.logo" class="preview mt-3">
                    <h5>Logo Preview:</h5>
                    <img :src="tournament.logo" alt="Tournament Logo Preview" class="img-fluid" />
                </div>
            </div>

            <!-- Max Number of Players -->
            <div class="mb-3">
                <label for="maxPlayers" class="form-label">Max Number of Players</label>
                <input type="number" id="maxPlayers" v-model="tournament.maxPlayers" class="form-control"
                    placeholder="Enter max number of players" min="2" required />
            </div>

            <!-- Registration End Date -->
            <div class="mb-3">
                <label for="registrationEndDate" class="form-label">Registration End Date</label>
                <input type="date" id="registrationEndDate" v-model="tournament.registrationEndDate"
                    class="form-control" required />
            </div>

            <!-- Competition Dates -->
            <div class="mb-3">
                <label for="competitionDates" class="form-label">Competition Dates</label>
                <input type="date" id="competitionStartDate" v-model="tournament.competitionStartDate"
                    class="form-control mb-2" required />
                <input type="date" id="competitionEndDate" v-model="tournament.competitionEndDate" class="form-control"
                    required />
            </div>

            <!-- Competition Location (Country and City) -->
            <div class="mb-3">
                <label for="country" class="form-label">Competition Location (Country)</label>
                <input type="text" id="country" v-model="tournament.country" class="form-control"
                    placeholder="Enter country" required />
            </div>

            <div class="mb-3">
                <label for="city" class="form-label">Competition Location (City)</label>
                <input type="text" id="city" v-model="tournament.city" class="form-control" placeholder="Enter city"
                    required />
            </div>

            <!-- Submit Button -->
            <div class="mb-3 text-center">
                <button type="submit" class="btn btn-primary">Create Tournament</button>
            </div>
        </form>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css'; 
export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournament: {
                name: '',
                logo: '',
                maxPlayers: 2,
                registrationEndDate: '',
                competitionStartDate: '',
                competitionEndDate: '',
                country: '',
                city: '',
            },
        };
    },
    methods: {
        onFileChange(e) {
            const file = e.target.files[0];
            if (file) {
                this.tournament.logo = URL.createObjectURL(file);
            }
        },
        submitForm() {
            console.log('Tournament Details:', this.tournament);
            // Here you can add logic to send form data to the backend
        },
    },
};
</script>

<style scoped>
.create-tournament {
    background-color: #f3eeea;
    padding: 40px;
    border-radius: 10px;
    max-width: 700px;
    margin: 0 auto;
}

h2 {
    font-family: 'Arial', sans-serif;
    color: #776b5d;
    text-align: center;
    margin-bottom: 30px;
}

.form-label {
    font-weight: bold;
    color: #776b5d;
}

.form-control,
.form-select {
    background-color: #ebe3d5;
    border: none;
    color: #776b5d;
}

.form-control:focus,
.form-select:focus {
    outline: none;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.btn-primary {
    background-color: #b0a695;
    border-color: #b0a695;
    color: white;
}

.btn-primary:hover {
    background-color: #776b5d;
}

.preview img {
    max-width: 200px;
    border-radius: 10px;
}
</style>