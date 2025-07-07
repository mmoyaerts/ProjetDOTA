import { createRouter, createWebHistory } from 'vue-router'
import LoginView       from '../LoginView.vue'
import SignUpView      from '../SignUpView.vue'
import Dashboard       from '../DashboardView.vue'
import DraftEditorView from '../DraftEditorView.vue'
import ProMatchesView  from '../ProMatchesView.vue'

const routes = [
    { path: '/',           name: 'Login',       component: LoginView },
    { path: '/signup',     name: 'SignUp',      component: SignUpView },
    { path: '/dashboard',  name: 'Dashboard',   component: Dashboard },
    { path: '/draft',      name: 'DraftEditor', component: DraftEditorView },
    { path: '/pro/:id',    name: 'ProMatches',  component: ProMatchesView, props: true },
    { path: '/draft/:matchId?',  
    name: 'DraftEditor',
    component: DraftEditorView,
    props: true                
  },
]

export default createRouter({
    history: createWebHistory(),
    routes,
})
