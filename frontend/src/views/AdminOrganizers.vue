<template>
    <Navbar />
    <div class="container mt-3">
      <h2>Admin Panel</h2>
    </div>
    <div class="admin-dashboard container mt-3">  
      <div v-if="activeTab === 'allOrganizers'" class="d-flex justify-content-between align-items-center mb-4">
        <h3>All Organizers</h3>
        <div class="tabs">
          <button class="btn btn-outline-dark tab-button" @click="activeTab = 'pendingApproval'" :class="{ active: activeTab === 'pendingApproval' }">Pending Approval</button>
        </div>
      </div>
      <table v-if="activeTab === 'allOrganizers'" class="table table-striped">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Date Verified</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="organizer in verifiedOrganizers" :key="organizer.id">
            <td>{{ organizer.name }}</td>
            <td>{{ organizer.email }}</td>
            <td>{{ formatDate(organizer.organizerDetails.dateVerified) }}</td>
          </tr>
        </tbody>
      </table>
  
      <div v-if="activeTab === 'pendingApproval'" class="d-flex justify-content-between align-items-center mb-4">
        <h3>Pending Organizers</h3>
        <div class="tabs">
          <button class="btn btn-outline-dark tab-button" @click="activeTab = 'allOrganizers'" :class="{ active: activeTab === 'allOrganizers' }">All Organizers</button>
        </div>
      </div>
        <table v-if="activeTab === 'pendingApproval'" class="table table-striped">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="organizer in pendingOrganizers" :key="organizer.id">
              <td>{{ organizer.name }}</td>
              <td>{{ organizer.email }}</td>
              <td>
                <button class="btn btn-success" @click="verifyOrganizer(organizer.id)">Verify</button>
              </td>
            </tr>
          </tbody>
        </table>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import Navbar from '@/components/NavbarComponent.vue';
  export default {
    components: {
        Navbar,
    },
    data() {
      return {
        activeTab: 'allOrganizers',
        organizers: [],
        verifiedOrganizers: [],
        pendingOrganizers: [],
      };
    },
    mounted() {
      this.fetchOrganizers();
    },
    methods: {
      formatDate(dateString) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(dateString).toLocaleDateString(undefined, options);
      },
      async fetchOrganizers() {
        try {
          const response = await axios.get('http://localhost:8080/organizers');
          console.log(response.data);
          this.organizers = response.data;

          this.verifiedOrganizers = this.organizers.filter(organizer => organizer.organizerDetails.dateVerified != null);
          this.pendingOrganizers = this.organizers.filter(organizer => organizer.organizerDetails.dateVerified == null);
        
        } catch (error) {
          console.error("Error fetching organizers:", error);
        }
      },
      async verifyOrganizer(id) {
        try {
          await axios.put(`http://localhost:8080/organizer/${id}`);
          this.fetchOrganizers(); 
        } catch (error) {
          console.error("Error verifying organizer:", error);
        }
      },
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
    
    .table {
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    }
  
    .tab-button {
        background-color: #b0a695;
        border: none;
        color: white;
        transition: background-color 0.3s, color 0.3s;
    }

    .tab-button:hover {
        background-color: #4b3d3d;
        color: white;
    }

    .btn-success {
        transition: background-color 0.3s, transform 0.2s;
    }

    .btn-success:hover {
        background-color: #28a745;
        transform: scale(1.05);
    }

  .btn-outline-dark.active {
    background-color: #776b5d;
    color: white;
  }
</style>