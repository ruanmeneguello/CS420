package com.getsimplex.steptimer.tcp;

import com.getsimplex.steptimer.model.DeviceInterest;
import com.getsimplex.steptimer.service.MessageIntake;
import com.getsimplex.steptimer.service.SessionValidator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TCPChannelHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Utils.log(ctx.channel().remoteAddress(), "Channel Active");
        ctx.channel().writeAndFlush("congratulations - you are connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        String[] messageParts = message.split(":");
        if(messageParts.length<2){
            Utils.log(ctx.channel().remoteAddress(),"Incomplete message, should be token:message, ex: 2130920923:scmurdock@gmail.com");
        } else if (messageParts.length==2){
            String token = messageParts[0];
            String tokenEmail = SessionValidator.emailFromToken(token);
            String email = messageParts[1];
            email=email.replaceAll("\r","");
            email=email.replaceAll("\n","");
            if (!tokenEmail.isEmpty() && tokenEmail.equals(email)){
                Utils.log(ctx.channel().remoteAddress(),"Socket subscribing to updates for :"+email);
                DeviceInterest deviceInterest = new DeviceInterest();
                deviceInterest.setInterestedChannel(ctx.channel());
                deviceInterest.setDeviceId(tokenEmail);//for TCP Socket requests we will use the user email as the device ID
                deviceInterest.setInterestedUser(tokenEmail);
                MessageIntake.route(deviceInterest);
            } else{
                Utils.log(ctx.channel().remoteAddress(),"Unable to subscribe to: "+email+" due to invalid token or mismatched email: "+tokenEmail);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Utils.log(ctx.channel().remoteAddress(), "Channel Inactive");
    }
}
