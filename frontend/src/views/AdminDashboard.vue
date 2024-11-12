<template>
    <!-- Navbar -->
    <Navbar />
    <div class="admin-dashboard container mt-3">
        <!-- Header Section -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>All Tournaments</h2>
        </div>

        <!-- Tabs for Organizer Role -->
        <div v-if="isOrganizer">
            <b-tabs v-model="activeTab" @change="fetchTournaments">
                <b-tab title="All Tournaments" />
                <b-tab title="My Tournaments" />
            </b-tabs>
        </div>

        <!-- Tournament List -->
        <div v-for="(tournament, index) in tournaments" :key="index" class="tournament-card row mb-3 p-3 hoverable"
            @click="viewTournament(tournament.id)">
            <div class="col-md-2 d-flex align-items-center">
                <img :src="tournament.logoUrl || defaultLogo" alt="Tournament Logo" class="img-fluid tournament-logo" />
            </div>
            <div class="col-md-8 d-flex flex-column justify-content-center">
                <h4>{{ tournament.name }}</h4>
                <p>{{ tournament.location }} | {{ tournament.date }}</p>
            </div>
            <div class="col-md-2 d-flex align-items-center">
                <span :class="getStatusClass(tournament.status)">{{ tournament.status }}</span>
            </div>
        </div>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarComponent.vue';
import axios from 'axios';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournaments: [], // Initialize as an empty array
            activeTab: 0, // Track the active tab
            defaultLogo: 'https://via.placeholder.com/80', // Placeholder logo
            role: localStorage.getItem("role") || "guest", // Get role from local storage
            organizerId: "anQ1ep6A96Wh5oNhidaJ", // Hardcoded organizer ID for demonstration
        };
    },
    computed: {
        isOrganizer() {
            return this.role === "organizer";
        },
    },
    methods: {
        getStatusClass(status) {
            switch (status) {
                case "Upcoming":
                    return "text-warning";
                case "In Progress":
                    return "text-success";
                case "Ended":
                    return "text-inactive";
                case "Cancelled":
                    return "text-danger";
                default:
                    return "text-muted";
            }
        },
        async fetchTournaments() {
            try {
                let response;
                if (this.isOrganizer && this.activeTab === 1) {
                    // Fetch tournaments for this organizer only
                    response = await axios.get(`http://localhost:8080/tournaments/organizer/${this.organizerId}`);
                } else {
                    // Fetch all tournaments
                    response = await axios.get('http://localhost:8080/tournaments');
                }
                this.tournaments = response.data.map(tournament => ({
                    ...tournament,
                    logoUrl: tournament.logoUrl || this.defaultLogo, // Set a default logo if none exists
                }));
                console.log("Tournaments fetched successfully.");
            } catch (error) {
                console.error("Error fetching tournaments:", error);
            }
        },
        viewTournament(id) {
            this.$router.push(`/tournament/${id}`);
        },
    },
    async mounted() {
        await this.fetchTournaments(); // Fetch tournaments when component mounts
    },
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
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.tournament-card:hover {
    background-color: #f7f7f7;
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

.text-warning {
    color: #FFA500;
}

.text-success {
    color: #28a745;
}

.text-danger {
    color: #dc3545;
}

.text-inactive {
    color: #6c757d;
}
</style>
