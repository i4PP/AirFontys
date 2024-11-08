export type Error = {
    response: {
        status: number;
        config: {
            url: string;
        };
        data: {
            error: string;
            path: string;
            timestamp: string;
            message: string;
        };
    }

}