import {useStorage} from "@vueuse/core";
import {User} from "../entities/User";



const user = useStorage<User>(
    'user',
    null,
    localStorage,
    {
        mergeDefaults: true,
    }
)

export default user;