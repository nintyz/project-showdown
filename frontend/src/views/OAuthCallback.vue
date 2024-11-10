<template>
  <div class="oauth-callback">
    <div v-if="loading" class="loading">
      Processing authentication...
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      loading: true
    };
  },
  async created() {
    const token = this.$route.query.token;
    const role = this.extractRoleFromToken(token);

    if (token) {
      localStorage.setItem('token', token);
      localStorage.setItem('role', role);
      this.$router.push('/dashboard');
    } else {
      try {
        const response = await axios.get('http://localhost:8080/oauth2/status');
        const { status, email } = response.data;

        if (status === 'unverified') {
          this.$router.push(`/verify?email=${email}`);
        } else if (status === 'requires_2fa') {
          this.$router.push(`/verify-2fa?email=${email}`);
        } else {
          this.$router.push('/login?error=invalid_state');
        }
      } catch (error) {
        this.$router.push('/login?error=authentication_failed');
      }
    }
  },
  extractRoleFromToken(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(window.atob(base64));
      return payload.role || 'player'; // Default to PLAYER if no role found
    } catch (error) {
      console.error('Error extracting role from token:', error);
      return 'player';
    }
  }
};
</script>