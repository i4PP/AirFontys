import { AxiosInstance } from 'axios';
import { FlightData } from '../entities/Flight/FlightData';
import { Error } from '../entities/Error';


interface FetchFlightsParams {
    depAirport: string;
    arrAirport: string;
    depDate: string;
}


export const fetchFlights = async (
    api: AxiosInstance,
    { depAirport, arrAirport, depDate }: FetchFlightsParams
): Promise<{ flights: FlightData[] | null; error: Error | null }> => {
    try {
        const response = await api.get<FlightData[]>('/flights', {
            params: {
                airportDep: depAirport,
                airportArrv: arrAirport,
                date: depDate,
            },
        });
        return { flights: response.data, error: null };
    } catch (err: any) {
        const error: Error = {
            response: {
                status: err.response?.status || 500,
                config: {
                    url: err.config.url,
                },
                data: {
                    error: err.response?.data.error || 'Unknown Error',
                    path: err.response?.data.path || '',
                    timestamp: new Date().toISOString(),
                    message: err.response?.data.message || 'An error occurred while fetching flights.',
                },
            },
        };
        return { flights: null, error };
    }
};
