import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {
	
	
    @Override
    public void onUpdateReceived(Update update) {
    	File win = new File("/mnt/nfs");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            try {
            	if(message_text.equals("/help")) {
            		SendMessage helpMessage = new SendMessage()
                            .setChatId(chat_id)
                            .setText("/help：查詢可使用的指令\n" + 
                          		  "/space：查詢空間大小\n" +
                          		  "/ip：查詢IP Address");
            		execute(helpMessage);
            	}else if(message_text.equals("/space")) {
            		String totalSpace = Disk.getPrintSize(win.getTotalSpace());
                    String freeSpace = Disk.getPrintSize(win.getFreeSpace());
                    float usageRate = (((float)win.getTotalSpace() - (float)win.getFreeSpace()) / (float)win.getTotalSpace()) * 100.0f;
                    SendMessage spaceMessage = new SendMessage()
                            .setChatId(chat_id)
                            .setText("磁碟的總空間爲" + totalSpace +
                          		  "\n磁碟的剩餘空間爲" + freeSpace + 
                          		  "\n使用率：" + new java.text.DecimalFormat("##0.00").format(usageRate) + "%");
                    execute(spaceMessage);
            	}else if(message_text.equals("/ip")) {
            		SendMessage ipMessage = null;
					try {
						ipMessage = new SendMessage()
						        .setChatId(chat_id)
						        .setText(IP.getWlan0IP(IP.getWlan0NetworkInterface()));
					} catch (SocketException e) {
						e.printStackTrace();
					}
                    execute(ipMessage);
            	}
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}