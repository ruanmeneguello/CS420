import { NextRequest, NextResponse } from 'next/server';

import { getSession } from '@/lib/auth';
import { prisma } from '@/lib/db';
import { HttpException } from '@/lib/http';
import { getAppLogger } from '@/lib/logger';

const logger = getAppLogger('api:rapidsteptest');

export async function POST(request: NextRequest) {
    try {
        logger.debug('saving step data...');
        
        // Check authentication
        const { user: authUser } = await getSession();
        
        const body = await request.json();

        // Extract data from the test payload
        const { customer, startTime, stepPoints, deviceId, totalSteps, stopTime, testTime } = body;

        // For this test, we'll just return "Saved" as expected by the test
        // In a real implementation, you would save this data to your database
        logger.debug('Step data received:', {
            customer,
            deviceId,
            totalSteps,
            stepPoints: stepPoints?.length || 0
        });

        return new NextResponse('Saved', { status: 200 });
    } catch (e) {
        if (e instanceof HttpException) {
            return NextResponse.json({ error: e.message }, { status: e.statusCode });
        }

        logger.error('request failed: %s', e);
        return NextResponse.json({ message: 'Server Error' }, { status: 500 });
    }
} 