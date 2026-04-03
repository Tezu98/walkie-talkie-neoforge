package pl.tezu.walkietalkie.client.gui.screen;

import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.client.gui.widget.CanalSlider;
import pl.tezu.walkietalkie.client.gui.widget.ToggleImageButton;
import pl.tezu.walkietalkie.client.gui.widget.EffectVolumeSlider;
import pl.tezu.walkietalkie.config.ModConfig;
import pl.tezu.walkietalkie.item.WalkieTalkieItem;
import pl.tezu.walkietalkie.network.ModMessages;
import pl.tezu.walkietalkie.network.packet.c2s.TransmitFromHandC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.walkietalkie.ButtonWalkieTalkieC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.walkietalkie.CanalWalkieTalkieC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class WalkieTalkieScreen extends Screen {

    private static WalkieTalkieScreen instance;

    private final int xSize = 195;
    private final int ySize = 110;

    private int guiLeft;
    private int guiTop;

    private final ItemStack stack;
    private boolean mute;
    private boolean activate;
    private int canal;

    private ToggleImageButton muteButton;
    private ToggleImageButton activateButton;
    private ToggleImageButton transmitFromHandButton;

    private CanalSlider canalSlider;
    private Button canalAddButton;
    private Button canalRemoveButton;
    private EffectVolumeSlider effectVolumeSlider;

    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/gui_walkietalkie.png");
    private static final ResourceLocation MUTE_TEXTURE = ResourceLocation.fromNamespaceAndPath("voicechat", "textures/icons/microphone_button.png");
    private static final ResourceLocation ACTIVATE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/activate.png");
    private static final ResourceLocation TRANSMIT_FROM_HAND_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/icons/hotbar_transmit.png");

    public WalkieTalkieScreen(ItemStack stack) {
        super(Component.translatable("gui.walkietalkie.title"));
        instance = this;
        this.stack = stack;

        mute = WalkieTalkieItem.isMute(stack);
        activate = WalkieTalkieItem.isActivate(stack);
        canal = WalkieTalkieItem.getCanal(stack);

        Minecraft.getInstance().setScreen(this);
        // Sync the client's saved preference to the server on open
        ModMessages.sendToServer(new TransmitFromHandC2SPacket(ModConfig.transmitFromHand));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;

        muteButton = new ToggleImageButton(guiLeft + 8, guiTop + 82, MUTE_TEXTURE, button -> sendButton(1, !mute), mute);
        this.addRenderableWidget(muteButton);

        activateButton = new ToggleImageButton(guiLeft + 30, guiTop + 82, ACTIVATE_TEXTURE, button -> sendButton(0, !activate), activate);
        this.addRenderableWidget(activateButton);

        transmitFromHandButton = new ToggleImageButton(guiLeft + 54, guiTop + 82, TRANSMIT_FROM_HAND_TEXTURE, button -> toggleTransmitFromHand(), ModConfig.transmitFromHand,
                (btn, context, font, mouseX, mouseY) -> context.renderTooltip(font, Component.translatable("gui.walkietalkie.transmit_from_hand.tooltip"), mouseX, mouseY)) {
            @Override
            protected void renderImage(GuiGraphics context, int mouseX, int mouseY) {
                com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                if (state) {
                    context.blit(texture, getX() + 2, getY() + 2, 16, 0, 16, 16, 32, 16);
                } else {
                    context.blit(texture, getX() + 2, getY() + 2, 0, 0, 16, 16, 32, 16);
                }
            }
        };
        this.addRenderableWidget(transmitFromHandButton);

        canalSlider = this.addRenderableWidget(new WTCanalSlider(this.width / 2 - 70, guiTop + 20, 140, 20, Component.empty()));

        canalAddButton = this.addRenderableWidget(Button.builder(Component.literal(">"), button -> sendCanal(canal + 1)).bounds(this.width / 2 - 10 + 80, guiTop + 20, 20, 20).build());
        canalRemoveButton = this.addRenderableWidget(Button.builder(Component.literal("<"), button -> sendCanal(canal - 1)).bounds(this.width / 2 - 10 - 80, guiTop + 20, 20, 20).build());

        effectVolumeSlider = this.addRenderableWidget(new WTEffectEffectVolumeSlider(this.width / 2 - 70, guiTop + 42, 140, 20, ModConfig.effectVolume));
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.blit(BG_TEXTURE, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawCenteredText(context, this.font, this.title, this.width / 2, guiTop + 7, 4210752);
    }

    protected void drawCenteredText(GuiGraphics context, Font font, Component component, int centerX, int y, int color) {
        context.drawString(font, component, centerX - font.width(component) / 2, y, color, false);
    }

    private void sendButton(int index, boolean activate) {
        ModMessages.sendToServer(new ButtonWalkieTalkieC2SPacket(index, activate));
    }

    private void sendCanal(int canal) {
        ModMessages.sendToServer(new CanalWalkieTalkieC2SPacket(canal));
    }

    private void toggleTransmitFromHand() {
        ModConfig.transmitFromHand = !ModConfig.transmitFromHand;
        ModConfig.save();
        ModMessages.sendToServer(new TransmitFromHandC2SPacket(ModConfig.transmitFromHand));
        transmitFromHandButton.setState(ModConfig.transmitFromHand);
    }

    public void updateButtons(ItemStack stack) {
        mute = WalkieTalkieItem.isMute(stack);
        activate = WalkieTalkieItem.isActivate(stack);
        canal = WalkieTalkieItem.getCanal(stack);

        muteButton.setState(WalkieTalkieItem.isMute(stack));
        activateButton.setState(WalkieTalkieItem.isActivate(stack));
        canalSlider.setCanal((WalkieTalkieItem.getCanal(stack)));

        canalAddButton.active = WalkieTalkieItem.getCanal(stack) != ModConfig.maxCanal;
        canalRemoveButton.active = WalkieTalkieItem.getCanal(stack) != 1;
    }

    public static WalkieTalkieScreen getInstance() {
        return instance;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        if (this.canalSlider.isHoveredOrFocused()) {
            this.canalSlider.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        if (this.effectVolumeSlider.isHoveredOrFocused()) {
            this.effectVolumeSlider.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return this.getChildAt(mouseX, mouseY).filter(element -> element.mouseReleased(mouseX, mouseY, button)).isPresent();
    }

    class WTCanalSlider extends CanalSlider {

        public WTCanalSlider(int x, int y, int width, int height, Component component) {
            super(x, y, width, height, component);
            sendCanal(canal);
        }

        @Override
        protected void updateCanal(int canal) {
            sendCanal(canal);
        }
    }

    static class WTEffectEffectVolumeSlider extends EffectVolumeSlider {

        public WTEffectEffectVolumeSlider(int x, int y, int width, int height, float initialVolume) {
            super(x, y, width, height, initialVolume);
        }

        @Override
        protected void onVolumeChanged(float volume) {
            ModConfig.effectVolume = volume;
            ModConfig.save();
        }
    }

}
