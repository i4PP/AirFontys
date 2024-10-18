import {useStorage} from "@vueuse/core";



const user = useStorage<User>(
    'user',
    null,
    localStorage,
    {
        mergeDefaults: true,
        serializer: {
            read: (v: any): LocalUser | null => v ? JSON.parse(atob(v)) : null,
            write: (v: any) => btoa(JSON.stringify(v)),
        },
    }
)

export default user;