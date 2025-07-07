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
        <router-link to="/">Mot de passe oublié?</router-link>
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
    // Appel à /api/auth/login
    const { data: token } = await axios.post('http://localhost:8080/login', {
      username: email.value,
      password: password.value
    })
    // Tu récupères ici le token ou la réponse renvoyée par ton service.verify()
    localStorage.setItem('jwt_token', token)
    router.push({ name: 'Dashboard' })
  } catch (err) {
    error.value = err.response?.data || 'Erreur de connexion'
  }
}
</script>