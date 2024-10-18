import { createRouter, createWebHistory} from "vue-router";
import Flights from "../views/Flights.vue";
import Login from "../views/Auth/Login.vue";
import user from '../localStorage/userStorage.ts'
import Register from '../views/Auth/Register.vue'

const routes = [
    {
        path: "/",
        component: Flights,
        alias: "/flights",
    },
    {
        path: "/flights/:fromAirport/:toAirport/:date",
        name: "Flights",
        component: Flights,
        props: true,
    },
    { path: "/login",
        component: Login,
        meta: {
            hideNav: true,
        },
        beforeEnter: (to, from, next) => {
            if (user.value) {
                next("/");
            } else {
                next();
            }
        }
    },
    {
        path: "/register",
        name: "register",
        component: Register,
        meta: { hideNav: true },
        beforeEnter: (to, from, next) => {
            if (user.value) {
                next("/");
            } else {
                next();
            }
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;