package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.requests.ResetRequest;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.utility.PixelRegion;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResetService {

    @Autowired private PixelOwnerRepo pixelOwnerRepo;
    @Autowired private RedisTemplate<byte[], byte[]> redis;

    @Autowired WebSocketService webSocketService;
    @Autowired ElasticService elasticService;

    public void resetPixels(Canvas canvas, ResetRequest resetRequest){
        if(resetRequest.getPixelList() != null && resetRequest.getPixelList().size() > 0){
            resetPixelList(canvas, resetRequest.getPixelList());
        }
        if(resetRequest.getPixelRegion() != null){
            resetPixelRegion(canvas, resetRequest.getPixelRegion());
        }
    }

    public void resetPixels(Canvas canvas, User user){
        pixelOwnerRepo.findAllByCanvasAndUser(canvas, user)
            .forEach(pixelOwner -> resetPixel(canvas, pixelOwner.getX(), pixelOwner.getY()));
    }

    private void resetPixelList(Canvas canvas, List<Integer[]> pixelList){
        pixelList.forEach(coordinate -> {
            resetPixel(canvas, coordinate[0], coordinate[1]);
        });
    }

    private void resetPixelRegion(Canvas canvas, PixelRegion pixelRegion){
        for(int x = pixelRegion.getMinX(); x <= pixelRegion.getMaxX(); x++){
            for(int y = pixelRegion.getMinY(); y <= pixelRegion.getMaxY(); y++){
                resetPixel(canvas, x, y);
            }
        }
    }

    private boolean isInsideCanvas(Canvas canvas, int x, int y){
        return  (canvas.getWidth() >= x && canvas.getHeight() >= y);
    }

    private void resetPixel(Canvas canvas, int x, int y){
        if(!isInsideCanvas(canvas, x, y))
            return;

        setPixelInBlob(canvas, x, y, (byte) 0);
        pixelOwnerRepo.deleteByXAndYAndCanvas(x, y, canvas);

        PlaceRequest placeRequest = PlaceRequest.builder()
                .x(x).y(y)
                .colorIndex(0).build();

        webSocketService.broadcastPixel(canvas, placeRequest);
        elasticService.logPixel(canvas.getId(), 0,
                placeRequest.getX(), placeRequest.getY(), placeRequest.getColorIndex(),
                true);

    }

    private void setPixelInBlob(Canvas canvas, int x, int y, byte color) {
        int offset = getBlobOffsetForPixel(canvas.getWidth(), canvas.getHeight(), x, y) * 8;
        var command = BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.UINT_8, BitFieldSubCommands.Offset.offset(offset), color);
        redis.opsForValue().bitField(canvas.getRedisKey().getBytes(), BitFieldSubCommands.create(command));
    }

    private int getBlobOffsetForPixel(int width, int height, int x, int y) {
        return y * width + x;
    }

}
