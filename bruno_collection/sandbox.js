const { faker } = require('@faker-js/faker/locale/en');

function createSteps(customer) {
    const startTime = Date.now();
    // between 10 and 30
    const totalSteps = faker.number.int({ min: 10, max: 30 });
    const stepPoints = Array.from({ length: totalSteps }, () => faker.number.int({ min: 150, max: 4000 }));
    const testDuration = faker.number.int({ min: 5000, max: 20000 });
    const stopTime = startTime + testDuration;
    const deviceId = faker.string.numeric(3);

    return {
        customer,
        startTime,
        stepPoints,
        stopTime,
        testTime: testDuration,
        totalSteps,
        // "deviceId": "007"
        deviceId,
    };
}

// fake timezone
console.log(createSteps(faker.internet.email().toLowerCase()));
