<template>
    <nav class="navbar navbar-expand-lg navbar-light">
        <div class="container-fluid">
            <router-link to="/all-tournaments-dashboard" class="navbar-brand">
                <img src="@/assets/logo.png" alt="Logo" class="logo" />
            </router-link>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <!-- Common Links for All Roles -->
                    <li class="nav-item" >
                       <router-link to="/all-tournaments-dashboard" class="nav-link"
                            :class="{ active: isActive('/all-tournaments-dashboard') }">All Tournaments</router-link>
                    </li>

                    <!-- Role-Specific Links -->
                    <li v-if="role === 'player'" class="nav-item">
                        <router-link to="/profile/player" class="nav-link"
                            :class="{ active: isActive('/profile/player') }">Player Profile</router-link>
                    </li>

                    
                    <li v-if="role === 'admin'" class="nav-item">
                        <router-link to="/all-players" class="nav-link"
                        :class="{ active: isActive('/all-players') }">All Players</router-link>
                    </li>
                    
                    <li v-if="role === 'admin'" class="nav-item">
                        <router-link to="/admin-organizers" class="nav-link"
                        :class="{ active: isActive('/admin-organizers') }">All Organizers</router-link>
                    </li>
                    
                    <li v-if="role === 'organizer'" class="nav-item">
                        <router-link to="/new-tournament" class="nav-link"
                        :class="{ active: isActive('/new-tournament') }">Add Tournament</router-link>
                    </li>
                    
                    <li v-if="role === 'organizer'" class="nav-item">
                        <router-link to="/profile/personalOrganizer/" class="nav-link"
                            :class="{ active: isActive('/profile/personalOrganizer') }">Profile</router-link>
                    </li>

                    <li v-if="role !== 'guest'" class="nav-item">
                        <button class="nav-link logout-btn" @click="logout">Logout</button>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script>
export default {
    data() {
        return {
            // role: "player",
            role: localStorage.getItem("role")
        };
    },
    computed: {

        profileRoute() {
            return this.role === 'player'
                ? '/profile/player'
                : this.role === 'organizer'
                ? '/profile/organizer'
                : '/profile/organizer';
        },
    },
    methods: {
        isActive(path) {
            return this.$route.path === path;
        },
        logout() {
            localStorage.clear();
            this.role = "guest";
            this.$router.push("/login");
        },
    },
};
</script>

<style scoped>
.logo {
    width: 120px;
    transition: transform 0.3s ease;
}
.logo:hover {
    transform: scale(1.1);
}

.navbar {
    background-color: #f8f9fa;
    border-bottom: 1px solid #ddd;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.navbar-nav .nav-link {
    margin: 0 15px;
    color: #555555;
    font-weight: 500;
    font-size: 16px;
    transition: color 0.3s ease;
}
.navbar-nav .nav-link:hover {
    color: #007bff;
    text-decoration: underline;
}

.navbar-nav .nav-link.active {
    font-weight: 600;
    color: #007bff;
    border-bottom: 2px solid #007bff;
}

.logout-btn {
    background: none;
    border: none;
    color: #555555;
    font-weight: 500;
    cursor: pointer;
    transition: color 0.3s ease, text-decoration 0.3s ease;
}
.logout-btn:hover {
    color: #007bff;
    text-decoration: underline;
}

@media (max-width: 768px) {
    .navbar-nav .nav-link,
    .logout-btn {
        margin: 10px 0;
        font-size: 15px;
    }
}
</style>
