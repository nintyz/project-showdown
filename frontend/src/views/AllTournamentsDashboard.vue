<template>
    <Navbar />
    <div class="admin-dashboard container mt-3">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>{{ isOrganizer && activeTab === 1 ? 'My Tournaments' : 'All Tournaments' }}</h2>
        </div>

        <div v-if="isOrganizer">
            <div class="d-flex justify-content-between align-items-center mb-4">
            <b-tabs v-model="activeTab" @input="fetchTournaments" class="custom-tabs" align="centered" pills
                nav-wrapper-class="custom-tab-wrapper">
                <b-tab title="All Tournaments"></b-tab>
                <b-tab title="My Tournaments"></b-tab>
            </b-tabs>
            <button class="btn btn-outline-dark" @click="addNewTournament">
                <i class="bi bi-plus"></i> Add New
            </button>
        </div>
        </div>
        <!-- <div v-else>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>All tournaments</h2>
            </div>

        </div> -->

        <div v-for="(tournament, index) in tournaments" :key="index"
            class="tournament-card row mb-4 p-4 align-items-center" @click="viewTournament(tournament.id)">
            <div class="col-md-2 text-center">
                <img :src="tournament.logoUrl || defaultLogo" alt="Tournament Logo" class="tournament-logo" />
            </div>
            <div class="col-md-7">
                <h4>{{ tournament.name }}</h4>
                <p>{{ tournament.country }} | {{ tournament.venue }}</p>
            </div>
            <div class="col-md-3 text-right">
                <span :class="['badge', getStatusBadgeClass(tournament.status)]">{{ tournament.status }}</span>
            </div>
        </div>
    </div>
</template>


<script>
import '@/assets/main.css';
import Navbar from '@/components/NavbarComponent.vue';
import axios from 'axios';

export default {
    components: {
        Navbar,
    },
    data() {
        return {
            tournaments: [],
            activeTab: 0,
            defaultLogo: 'https://via.placeholder.com/100',
            role: localStorage.getItem("role")|| "guest",
            userId: localStorage.getItem("userId"),
        };
    },
    computed: {
        isOrganizer() {
            return this.role === "organizer";
        },
    },
    watch: {
        activeTab(newTab) {
            console.log("Tab switched to:", newTab);
            this.fetchTournaments(); // Fetch tournaments based on the new tab
        },
    },
    methods: {
        addNewTournament() {
            this.$router.push("/new-tournament");
        },
        async fetchTournaments() {
            try {
                console.log('Fetching tournaments, Active Tab:', this.activeTab);
                let response;
                if (this.isOrganizer && this.activeTab === 1) {
                    // Fetch tournaments for this organizer only
                    response = await axios.get(`http://localhost:8080/tournaments/organizer/${this.userId}`);
                } else {
                    // Fetch all tournaments
                    response = await axios.get('http://localhost:8080/tournaments');
                }
                this.tournaments = response.data.map(tournament => ({
                    ...tournament,
                    logoUrl: tournament.logoUrl || this.defaultLogo,
                }));
                console.log("Tournaments fetched:", this.tournaments);
            } catch (error) {
                console.error("Error fetching tournaments:", error);
            }
        },
        viewTournament(id) {
            this.$router.push(`/tournament/${id}`);
        },
        getStatusBadgeClass(status) {
            switch (status) {
                case "Registration":
                    return "badge-warning";
                case "In Progress":
                    return "badge-success";
                case "Ended":
                    return "badge-secondary";
                case "Cancelled":
                    return "badge-danger";
                default:
                    return "badge-light";
            }
        },
    },
    mounted() {
        this.fetchTournaments();
        console.log("Organizer ID:", this.userId);
    },
};
</script>



<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap');

.admin-dashboard {
    background-color: #f3eeea;
    padding: 30px;
    font-family: 'Roboto', sans-serif;
}

h2 {
    color: #343a40;
    font-weight: 700;
}

.custom-tabs .nav-item .nav-link {
    color: #495057;
    font-size: 16px;
    font-weight: 500;
    padding: 10px 20px;
    border-radius: 50px;
    transition: background-color 0.3s, color 0.3s;
}

.custom-tabs .nav-item .nav-link.active {
    background-color: #776b5d !important;
    color: #fff !important;
}

.custom-tabs .nav-item .nav-link:hover {
    background-color: #776b5d;
    color: #fff !important;
}

.tournament-card {
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
}

.tournament-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.tournament-logo {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
}

.tournament-card h4 {
    font-size: 22px;
    color: #343a40;
    margin-bottom: 8px;
}

.tournament-card p {
    margin: 0;
    font-size: 16px;
    color: #6c757d;
}

.badge {
    font-size: 14px;
    padding: 8px 12px;
    border-radius: 12px;
}

.badge-warning {
    background-color: #ffc107;
    color: #212529;
}

.badge-success {
    background-color: #28a745;
}

.badge-secondary {
    background-color: #6c757d;
}

.badge-danger {
    background-color: #dc3545;
}
</style>
