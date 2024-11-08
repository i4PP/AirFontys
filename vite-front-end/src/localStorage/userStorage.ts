import { useStorage } from "@vueuse/core";
import { User } from "../entities/User";

const user = useStorage<User | null>(
    'user',                // Key
    null,                  // Default value can be `null`
    localStorage,          // Storage type (localStorage)
    {
        mergeDefaults: true,
        serializer: {
            read: (v: any): User | null => v ? JSON.parse(atob(v)) : null,   // Handle reading (decoding) from storage
            write: (v: User | null) => v ? btoa(JSON.stringify(v)) : '',     // Handle writing (encoding) to storage
        },
    }
);

export default user;