<template>
  <div class="container">
    <div class="card">
      <img src="../assets/logo.svg" alt="Logo" class="logo" />
      <h1>Connexion</h1>
      <form @submit.prevent="onSubmit">
        <label>Email</label>
        <input v-model="email" type="text" required />
        <label>Mot de passe</label>
        <input v-model="password" type="password" required />
        <button type="submit">Se connecter</button>
      </form>
      <p v-if="error" style="color: #f87171; text-align: center;">{{ error }}</p>
      <div class="link-group">
        <router-link to="/signup">Créer un compte</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

async function onSubmit() {
  error.value = ''
  try {
    const { data: token } = await axios.post('http://localhost:8080/login', {
      email: email.value,
      password: password.value
    },
    {
      withCredentials: true
    })
    localStorage.setItem('jwt_token', token.token)
    router.push({ name: 'Dashboard' })
  } catch (err) {
    error.value = err.response?.data || 'Erreur de connexion'
  }
}
</script>