package aedbia.showKey.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShowKeyCommandThread extends Thread{
    private static final Logger LOGGER = LogManager.getLogger();
    public ShowKeyCommandThread() {
        super("ShowKeyCommandThread");
        start();
    }
    public static boolean stop = false;
    @SuppressWarnings("FieldMayBeFinal")
    private List<KeyMapping> keyMapping = new ArrayList<>();
    @Override
    public void run() {
        LOGGER.debug("ShowKeyCommandThread"+" start!");
        while (!stop) {
            //this.wait(1);
            for (KeyMapping keyMapping: Minecraft.getInstance().options.keyMappings){
                if(keyMapping.isDown()){
                    if (!this.keyMapping.contains(keyMapping)&&Minecraft.getInstance().player != null) {
                        this.keyMapping.add(keyMapping);
                        //noinspection NullableProblems
                        Minecraft.getInstance().gui.getChat().addMessage(new Component() {
                            @Override
                            public Style getStyle() {
                                return Style.EMPTY.withUnderlined(true)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,this.getString()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new TranslatableComponent("tips.show_key.click_to_copy")));
                            }

                            @Override
                            public String getContents() {
                                return keyMapping.getName();
                            }

                            @Override
                            public List<Component> getSiblings() {
                                return new ArrayList<>();
                            }

                            @Override
                            public MutableComponent plainCopy() {
                                return null;
                            }

                            @Override
                            public MutableComponent copy() {
                                return null;
                            }

                            @Override
                            public FormattedCharSequence getVisualOrderText() {
                                return FormattedCharSequence.EMPTY;
                            }
                        });
                    }
                }else this.keyMapping.remove(keyMapping);
            }
        }
        LOGGER.debug("ShowKeyCommandThread"+" stop!");
    }
}
