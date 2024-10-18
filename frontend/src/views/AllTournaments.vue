<template>
    <!-- Navbar -->
    <Navbar />
    <div class="user-tournaments container mt-3">
        <!-- Header Section -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Available Tournaments</h2>
        </div>

        <!-- Tournament List -->
        <div v-for="(tournament, index) in tournaments" :key="index" class="tournament-card row mb-3 p-3">
            <div class="col-md-2 d-flex align-items-center">
                <img :src="tournament.logo" alt="Tournament Logo" class="img-fluid tournament-logo" />
            </div>
            <div class="col-md-6 d-flex flex-column justify-content-center">
                <h4>{{ tournament.name }}</h4>
                <p>{{ tournament.location }} | {{ tournament.date }}</p>
            </div>
            <div class="col-md-2 d-flex align-items-center">
                <span :class="getStatusClass(tournament.status)">{{ tournament.status }}</span>
            </div>
            <div class="col-md-2 d-flex justify-content-end align-items-center">
                <button class="btn btn-outline-primary me-2" @click="viewTournament(tournament)">View</button>
            </div>
        </div>
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
            tournaments: [
                {
                    name: "ATP Finals",
                    location: "Turin, Italy",
                    date: "10 - 25 October 2024",
                    status: "Ongoing",  // Status changed to "Ended"
                    logo: "https://cdn.cookielaw.org/logos/d650bf03-392a-4f58-9e0f-30e4e5889bc1/4166a46d-b503-4394-9a8d-cfc6856bb183/d11831c3-5497-41f0-bcc8-a7d664ee017f/nitto.png",
                    redirectTo: "/dashboard",  // Redirect for ended tournament
                },
                {
                    name: "Wimbledon",
                    location: "London, UK",
                    date: "1 - 14 JDecember 2024",
                    status: "Upcoming",
                    logo: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg7iZb7c9r9kR4Quz_H0M9bgyeYyaPvJxJ7_hANon_A2V0EqjG1PmkuXowyF_ums6icjk&usqp=CAU",
                    redirectTo: "/dashboard-register",  // Redirect for upcoming tournament registration
                },
            ],
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
        viewTournament(tournament) {
            // Redirect to specific page depending on the tournament
            this.$router.push(tournament.redirectTo);
        },
    },
};
</script>

<style scoped>
.user-tournaments {
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

.text-warning {
    color: #FFA500;
}

.text-success {
    color: #28a745;
}

.text-danger {
    color: #dc3545;
}
</style>
