import { createRouter, createWebHistory } from 'vue-router'

import LoginPage from '@/views/LoginPage.vue'
import DashboardPage from '@/views/DashboardPage.vue'
import PlayerProfile from '@/views/PlayerProfile.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import AddTournament from '@/views/AddTournament.vue'
import SignUp from '@/views/SignUp.vue'
import DashboardPageTwo from '@/views/DashboardPageTwo.vue'
import AddUserDetails from '@/views/AddUserDetails.vue'
import AllTournaments from '@/views/AllTournaments.vue'
import TournamentDetails from '@/views/TournamentDetails.vue'
import TournamentDashboard from '@/views/TournamentDashboard.vue'
import EditTournament from '@/views/EditTournament.vue'

const routes = [
    {
        path: '/login',
        name: 'LoginPage',
        component: LoginPage,
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: SignUp,
    },
    {
        path: '/user-details',
        name: 'AddUserDetails',
        component: AddUserDetails,
    },
    {
        path: '/dashboard',
        name: 'DashboardPage',
        component: DashboardPage,
    },
    {
        path: '/dashboard-register',
        name: 'DashboardPageTwo',
        component: DashboardPageTwo,
    },
    {
        path: '/player-profile',
        name: 'PlayerProfile',
        component: PlayerProfile,
    },
    {
        path: '/admin-dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
    },
    {
        path: '/new-tournament',
        name: 'AddTournament',
        component: AddTournament,
    },
    {
        path: '/all-tournaments',
        name: 'AllTournaments',
        component: AllTournaments,
    },
    {
        path: '/tournament/:id',
        component: TournamentDetails,
    },
    {
        path: '/tournament/:id/dashboard',
        component: TournamentDashboard,
    },
    {
        path: '/tournament/:id/edit',
        component: EditTournament,
    },
    

]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
