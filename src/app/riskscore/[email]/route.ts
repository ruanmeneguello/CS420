import { NextRequest, NextResponse } from 'next/server';

import { getSession } from '@/lib/auth';
import { HttpException } from '@/lib/http';
import { getAppLogger } from '@/lib/logger';

const logger = getAppLogger('api:riskscore');

type RouteParams = {
    email: string;
};

type PageProps = {
    params: Promise<RouteParams>;
};

export async function GET(_: NextRequest, { params }: PageProps) {
    try {
        logger.debug('calculating risk score...');
        
        // Check authentication
        const { user: authUser } = await getSession();
        
        const { email } = await params;

        // For this test, we'll return a mock risk score
        // In a real implementation, you would calculate this based on the user's step data
        const mockScore = Math.floor(Math.random() * 100) + 1; // Random score between 1-100

        logger.debug('Risk score calculated for %s: %d', email, mockScore);

        return NextResponse.json({ score: mockScore }, { status: 200 });
    } catch (e) {
        if (e instanceof HttpException) {
            return NextResponse.json({ error: e.message }, { status: e.statusCode });
        }

        logger.error('request failed: %s', e);
        return NextResponse.json({ message: 'Server Error' }, { status: 500 });
    }
} 