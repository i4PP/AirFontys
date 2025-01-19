describe('Register Page test', () => {
    const fillRegisterForm = (email, password, confirmPassword, firstname, lastname) => {
        cy.get('input[name="email"]').type(email);
        cy.get('input[name="password"]').type(password);
        cy.get('input[name="confirmPassword"]').type(confirmPassword);
        cy.get('input[name="firstname"]').type(firstname);
        cy.get('input[name="lastname"]').type(lastname);
        cy.get('button[type="submit"]').click();
    };

    const verifyErrorMessage = (message) => {
        cy.get('.text-red-500').should('contain', message);
    };

    beforeEach(() => {
        cy.visit('/register');
        cy.clearLocalStorage();

    });

    it('can register with matching passwords', () => {
        fillRegisterForm('test@cypress.com', 'ThisIsAPasswordTest12!', 'ThisIsAPasswordTest12!', 'Test', 'Cypress');

        cy.wait(1000);
        cy.getAllLocalStorage().then((ls) => {
            const storage = ls['http://localhost:5173'];
            expect(storage).to.have.property('user');
        });

        cy.url().should('eq', 'http://localhost:5173/');
    });

    it('shows an error when passwords do not match', () => {
        fillRegisterForm('test1@cypress.com', 'ThisIsAPasswordTest12!', 'DifferentPassword123!', 'Test', 'Cypress');
        verifyErrorMessage('Passwords do not match');
        cy.url().should('eq', 'http://localhost:5173/register');
    });

    it('shows an error when email is already registered', () => {
        fillRegisterForm('test@cypress.com', 'ThisIsAPasswordTest12!', 'ThisIsAPasswordTest12!', 'Test', 'Cypress');
        verifyErrorMessage('User with this email already exists.');
        cy.url().should('eq', 'http://localhost:5173/register');
    });

    it('shows an error when password is not strong enough', () => {
        fillRegisterForm('test1@cypress.com', 'weakpassword', 'weakpassword', 'Test', 'Cypress');
        verifyErrorMessage('Password does not meet the required strength.');
        cy.url().should('eq', 'http://localhost:5173/register');
    });
});
