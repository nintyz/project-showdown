<template>
    <!-- Navbar -->
    <Navbar />
    <div class="admin-dashboard container mt-3">
        <!-- Header Section -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>All tournaments</h2>
            <button class="btn btn-outline-dark" @click="addNewTournament">
                <i class="bi bi-plus"></i> Add New
            </button>
        </div>

        <!-- Tournament List -->
        <div v-for="(tournament, index) in tournaments" :key="index" class="tournament-card row mb-3 p-3">
            <div class="col-md-2 d-flex align-items-center">
                <img :src="tournament.logoUrl" alt="Tournament Logo" class="img-fluid tournament-logo" />
            </div>
            <div class="col-md-6 d-flex flex-column justify-content-center">
                <h4>{{ tournament.name }}</h4>
                <p>{{ tournament.location }} | {{ tournament.date }}</p>
            </div>
            <div class="col-md-2 d-flex align-items-center">
                <span :class="getStatusClass(tournament.status)">{{ tournament.status }}</span>
            </div>
            <div class="col-md-2 d-flex justify-content-end align-items-center">
                <button class="btn btn-outline-primary me-2" @click="viewTournament(tournament.id)">view</button>
                <button class="btn btn-outline-secondary me-2" @click="editTournament(tournament.id)">edit</button>
                <button class="btn btn-icon" @click="deleteTournament(tournament.id)">
                    <img src="@/assets/remove.png" alt="Icon" />
                </button>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import Navbar from '@/components/NavbarComponent.vue';
import '@/assets/main.css';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournaments: [], // Initialize as an empty array
        };
    },
    methods: {
        getStatusClass(status) {
            switch (status) {
                case "Upcoming":
                    return "text-warning";
                case "Ongoing":
                    return "text-success";
                case "Ended":
                    return "text-danger";
                default:
                    return "text-muted";
            }
        },
        addNewTournament() {
            this.$router.push("/new-tournament");
        },
        // fetchTournaments method in <script> section
        async fetchTournaments() {
            try {
                const response = await axios.get('http://localhost:8080/tournaments'); // Replace with your API endpoint
                this.tournaments = response.data.map(tournament => ({
                    ...tournament,
                    logoUrl: tournament.logoUrl, // Fallback to placeholder if no logoUrl
                }));
            } catch (error) {
                console.error("Error fetching tournaments:", error);
            }
        },

        viewTournament(id) {
            this.$router.push(`/tournament/${id}`);
        },
        editTournament(id) {
            this.$router.push(`/tournament/${id}/edit`);
        },
        async deleteTournament(id) {
            try {
                await axios.delete(`http://localhost:8080/tournament/${id}`); // Replace with your API endpoint
                this.tournaments = this.tournaments.filter(tournament => tournament.id !== id);
                console.log(`Tournament with ID ${id} deleted successfully.`);
            } catch (error) {
                console.error("Error deleting tournament:", error);
            }
        },
    },
    async mounted() {
        localStorage.setItem("role", "admin");
        await this.fetchTournaments(); // Fetch tournaments when component mounts
    }
};
</script>

<style scoped>
.admin-dashboard {
    background-color: #f3eeea;
    padding: 30px;
}

h2 {
    font-family: 'Arial', sans-serif;
    color: #776b5d;
}

.tournament-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.tournament-logo {
    width: 80px;
    height: auto;
}

.tournament-card h4 {
    font-size: 18px;
    color: #776b5d;
}

.tournament-card p {
    margin: 0;
    font-size: 14px;
    color: #776b5d;
}

.btn-outline-primary {
    background-color: #b0a695;
    color: white;
    border: none;
}

.btn-outline-primary:hover {
    background-color: #776b5d;
}

.btn-outline-secondary {
    border-color: #776b5d;
    color: #776b5d;
}

.btn-outline-secondary:hover {
    background-color: #776b5d;
    color: white;
}

.btn-danger {
    color: white;
}

.text-warning {
    color: #FFA500;
}

.text-success {
    color: #28a745;
}

.text-danger {
    color: #dc3545;
}

.btn-icon img {
    width: 24px;
    height: 24px;
}
</style>
