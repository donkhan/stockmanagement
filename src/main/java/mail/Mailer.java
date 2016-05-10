package mail;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import util.InternetService;

public class Mailer {

	public void mail(String fileName,StringBuffer subject) {
		mail(fileName,subject,null);
	}
	
	private Properties getSMTPProperties(){
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		return props;
	}
	
	private Session getSession(){
		Session session = Session.getInstance(getSMTPProperties(),
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("routetokamil","");
				}
			}
		);
		return session;
	}
		
	public void mail(String fileName,StringBuffer subject,StringBuffer content){
		if(!InternetService.up()){
			System.out.println("Internet is not up... Hence i am not going to send mail");
			return;
		}
		try {
			Message message = new MimeMessage(getSession());
			message.setFrom(new InternetAddress("secret-friend@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("routetokamil@gmail.com"));
			message.setSubject(subject.toString());
	        
			if(fileName != null){
				Multipart multipart = new MimeMultipart();
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(fileName);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName("Bulletin.html");
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
			}else{
				if(content != null){
					message.setText(content.toString());
				}else{
					message.setText("");
				}
			}
			
			System.out.println("Sending email");
			Transport.send(message);
	        System.out.println("Done");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}catch(Throwable t){
			t.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String args[]){
		Mailer mailer = new Mailer();
		StringBuffer subject = new StringBuffer(); subject.append("Test Subject");
		StringBuffer content = new StringBuffer(); content.append("Summa Summa");
		mailer.mail("c://users//kkhan//sweeties///carmen//Trade.xls", subject,content);
		//mailer.mail(null,subject,content);
	}

}
