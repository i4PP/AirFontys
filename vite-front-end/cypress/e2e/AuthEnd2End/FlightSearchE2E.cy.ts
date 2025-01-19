describe('Register Page test', () => {

    beforeEach(() => {
        cy.visit('/');
    });

    it('Search for a flight', () => {
        cy.get('input[name="depAirport"]').type('JFK');
        cy.get('input[name="arrAirport"]').type('AMS');
        cy.get('input[name="depDate"]').type('2028-01-01');
        cy.get('button[type="submit"]').click();
        // Checck the url
        cy.url().should('include', 'http://localhost:5173/?depAirport=JFK&arrAirport=AMS&depDate=2028-01-01');
        // Check the search results
        cy.get('[data-testid="flight-card"]').should('have.length', 4);




    });

});
