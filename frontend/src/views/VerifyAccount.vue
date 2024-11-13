<template>
  <div class="verify-container">
    <div class="verify-box">
      <img src="@/assets/logo.png" alt="Logo" class="logo">

      <h2>Verify Your Account</h2>
      <p class="instruction">
        We've sent a verification code to <strong>{{ email }}</strong>
      </p>

      <div class="verification-form">
        <!-- Email Verification Code Input -->
        <div class="code-input" v-if="!isTwoFactorStep">
          <input type="text" v-model="verificationCode" placeholder="Enter verification code" maxlength="6"
            :disabled="isLoading" @keypress="numberOnly($event)" />
        </div>

        <button @click="verifyAccount" :disabled="isLoading || !isValidCode" class="verify-btn" v-if="!isTwoFactorStep">
          {{ isLoading ? 'Verifying...' : 'Verify Account' }}
        </button>

        <!-- Resend Code Button with Cooldown -->
        <div v-if="!isTwoFactorStep && !isEmailVerified" class="resend-section">
          <button @click="resendCode" :disabled="isLoadingResend || resendCooldown > 0" class="resend-btn">
            {{ isLoadingResend ? 'Sending...' : resendCooldown > 0 ? `Resend code in ${resendCooldown}s` : 'Resend Code'
            }}
          </button>
        </div>

        <!-- Optional 2FA Enable Prompt -->
        <div v-if="isEmailVerified && !isTwoFactorStep && source === 'signup'">
          <p>Would you like to enable Two-Factor Authentication (2FA)?</p>
          <div class="two-factor-buttons">
            <button @click="enable2FA" class="enable-2fa-btn">Enable 2FA</button>
            <button @click="skip2FA" class="skip-2fa-btn">Skip</button>
          </div>
        </div>

        <!-- QR Code Display for 2FA Setup -->
        <div v-if="isTwoFactorStep">
          <h3>Scan this QR Code with your Authenticator App</h3>
          <img :src="qrCodeImage" alt="QR Code for 2FA" class="qr-code" />
          <button @click="finalize2FASetup" class="proceed-2fa-btn">Proceed</button>
        </div>

        <!-- Display Success/Error Messages -->
        <div v-if="error" class="error-message">{{ error }}</div>
        <div v-if="success" class="success-message">{{ success }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      email: '',
      verificationCode: '',
      error: null,
      success: null,
      isLoading: false,
      isLoadingResend: false,
      isEmailVerified: false,
      isTwoFactorStep: false,
      qrCodeImage: '',
      resendCooldown: 0,
      source: this.$route.query.source || 'login',
      role: localStorage.getItem("role"),
    };
  },
  created() {
    this.email = this.$route.query.email;
    if (!this.email) {
      this.$router.push('/signup');
    }
  },
  computed: {
    isValidCode() {
      return this.verificationCode.length === 6 && /^\d+$/.test(this.verificationCode);
    }
  },
  methods: {
    async verifyAccount() {
      this.isLoading = true;
      this.error = null;
      this.success = null;

      try {
        const response = await axios.post('http://localhost:8080/verify', {
          email: this.email,
          verificationCode: this.verificationCode
        });

        this.success = response.data.message || 'Email verified successfully!';
        this.isEmailVerified = true;
        this.resendCooldown = 0; // Hide the resend section
        if (response.data.token) {
          localStorage.setItem("token", response.token);
        }
        if (response.data.status === 'requires_2fa') {
          this.$router.push({ path: '/verify-2fa', query: { email: this.email } });
        }
      } catch (error) {
        this.error = error.response?.data || 'Verification failed. Please try again.';
      } finally {
        this.isLoading = false;
      }
    },
    async resendCode() {
      if (this.resendCooldown > 0) return; // Prevent multiple clicks

      this.isLoadingResend = true;
      this.error = null;
      this.success = null;

      try {
        await axios.post('http://localhost:8080/resend', null, {
          params: { email: this.email }
        });

        this.success = 'New verification code sent!';
        this.startResendCooldown();
      } catch (error) {
        this.error = error.response?.data || 'Failed to resend code. Please try again.';
      } finally {
        this.isLoadingResend = false;
      }
    },
    startResendCooldown() {
      this.resendCooldown = 60;
      const interval = setInterval(() => {
        if (this.resendCooldown > 0) {
          this.resendCooldown--;
        } else {
          clearInterval(interval);
        }
      }, 1000);
    },
    async enable2FA() {
      try {
        const response = await axios.post('http://localhost:8080/enable-2fa', null, {
          params: { email: this.email }
        });
        this.qrCodeImage = `data:image/png;base64,${response.data.qrCodeImage}`;
        this.isTwoFactorStep = true;
      } catch (error) {
        console.error("Error enabling 2FA:", error.response?.data || error.message);
      }
    },
    skip2FA() {
      this.redirectToLanding();
    },
    finalize2FASetup() {
      this.redirectToLanding();
    },
    redirectToLanding() {
      this.$router.push('/all-tournaments-dashboard');
    },
    numberOnly(event) {
      const charCode = event.which ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        event.preventDefault();
      }
    }
  }
};
</script>

<style scoped>
.redirect-countdown {
  font-size: 0.9rem;
  margin-top: 0.5rem;
  color: #666;
}

.verify-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f3eeea;
}

.verify-box {
  background-color: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.logo {
  width: 150px;
  margin-bottom: 1.5rem;
}

h2 {
  color: #776b5d;
  margin-bottom: 1rem;
}

.instruction {
  color: #666;
  margin-bottom: 1.5rem;
}

.code-input input {
  width: 100%;
  padding: 0.8rem;
  margin-bottom: 1rem;
  border: 1px solid #b0a695;
  border-radius: 5px;
  font-size: 1rem;
  text-align: center;
  letter-spacing: 0.2rem;
}

.code-input input:focus {
  outline: none;
  border-color: #776b5d;
  box-shadow: 0 0 0 2px rgba(119, 107, 93, 0.2);
}

.verify-btn,
.enable-2fa-btn,
.skip-2fa-btn,
.proceed-2fa-btn,
.resend-btn {
  width: 100%;
  padding: 0.8rem;
  margin-top: 0.5rem;
  background-color: #776b5d;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.enable-2fa-btn,
.skip-2fa-btn {
  background-color: #f3c623;
}

.proceed-2fa-btn {
  background-color: #4caf50;
}

.verify-btn:disabled,
.enable-2fa-btn:disabled,
.skip-2fa-btn:disabled,
.proceed-2fa-btn:disabled,
.resend-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.two-factor-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.two-factor-buttons button {
  flex: 1;
  margin: 0 0.5rem;
}

.qr-code {
  width: 150px;
  height: 150px;
  margin: 1rem 0;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  padding: 0.5rem;
  border-radius: 5px;
  margin-top: 1rem;
}

.success-message {
  color: #28a745;
  background-color: #d4edda;
  padding: 0.5rem;
  border-radius: 5px;
  margin-top: 1rem;
}
</style>