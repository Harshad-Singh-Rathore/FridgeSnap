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

# Define custom dataset class
class CustomDataset(Dataset):
    def __init__(self, image_paths, transform=None):
        self.image_paths = image_paths
        self.transform = transform

    def __len__(self):
        return len(self.image_paths)

    def __getitem__(self, idx):
        img_path = self.image_paths[idx]
        image = Image.open(img_path).convert("RGB")
        if self.transform:
            image = self.transform(image)
        return image

# Define transformation for input images
transform = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor()
])

# Create dataset and dataloader
image_paths = ["output.png"]  # Add your input image paths
dataset = CustomDataset(image_paths, transform=transform)
dataloader = DataLoader(dataset, batch_size=1, shuffle=False)

# Initialize U-Net model
model = UNet()

# Load pre-trained weights if available
model.load_state_dict(torch.load("unet_model.pth"))

# Set model to evaluation mode
model.eval()

# Iterate over dataset and perform segmentation
for i, inputs in enumerate(dataloader):
    # Forward pass through the model
    outputs = model(inputs)
    # Convert outputs to numpy array and save as PNG
    output_image = transforms.ToPILImage()(outputs.squeeze(0).detach().cpu())
    output_image.save(f"output_{i}.png")
