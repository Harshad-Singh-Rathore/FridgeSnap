import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader
import torchvision.transforms as transforms
from PIL import Image
import numpy as np

# Define U-Net architecture
class UNet(nn.Module):
    def __init__(self):
        super(UNet, self).__init__()
        # Define encoder (downsampling)
        self.encoder = nn.Sequential(
            nn.Conv2d(3, 64, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(64, 64, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.MaxPool2d(kernel_size=2, stride=2)
        )
        
        # Define decoder (upsampling)
        self.decoder = nn.Sequential(
            nn.Conv2d(64, 64, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.Conv2d(64, 64, kernel_size=3, padding=1),
            nn.ReLU(inplace=True),
            nn.ConvTranspose2d(64, 3, kernel_size=2, stride=2)
        )
    
    def forward(self, x):
        # Forward pass through encoder
        x = self.encoder(x)
        # Forward pass through decoder
        x = self.decoder(x)
        return x

# Define custom dataset class for training
class CustomDataset(Dataset):
    def __init__(self, image_paths, target_paths, transform=None):
        self.image_paths = image_paths
        self.target_paths = target_paths
        self.transform = transform

    def __len__(self):
        return len(self.image_paths)

    def __getitem__(self, idx):
        img_path = self.image_paths[idx]
        target_path = self.target_paths[idx]
        image = Image.open(img_path).convert("RGB")
        target = Image.open(target_path).convert("RGB")
        if self.transform:
            image = self.transform(image)
            target = self.transform(target)
        return image, target

# Define transformation for input images
transform = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor()
])

# Create dataset and dataloader for training
train_image_paths = []
train_target_paths = []
for i in range(0,30) :
    train_image_paths.append("train/input/"+str(i)+".png")
    train_target_paths.append("train/output/"+str(i)+".png")
#train_image_paths = ["input1.png", "input2.png"]  # Add your train input image paths
#train_target_paths = ["0.png", "1.png"]  # Add your train target image paths
train_dataset = CustomDataset(train_image_paths, train_target_paths, transform=transform)
train_dataloader = DataLoader(train_dataset, batch_size=1, shuffle=True)

# Initialize U-Net model
model = UNet()

# Define loss function and optimizer
criterion = nn.MSELoss()
optimizer = optim.Adam(model.parameters(), lr=0.001)

# Train the model
num_epochs = 10
for epoch in range(num_epochs):
    model.train()
    running_loss = 0.0
    for i, (inputs, targets) in enumerate(train_dataloader):
        optimizer.zero_grad()
        outputs = model(inputs)
        loss = criterion(outputs, targets)
        loss.backward()
        optimizer.step()
        running_loss += loss.item()
    print(f"Epoch [{epoch+1}/{num_epochs}], Loss: {running_loss/len(train_dataloader)}")

# Save trained model
torch.save(model.state_dict(), "unet_model.pth")
