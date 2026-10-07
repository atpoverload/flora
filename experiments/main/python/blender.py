import logging

import bpy

from flora_rendering_problem_service_pb2 import RenderingConfiguration


class BlenderSceneRenderer:
    def __init__(self, scene: bpy.types.Scene):
        self.scene = scene

    def set_render_settings(self, device: str):
        self.device = device

        self.scene.display_settings.display_device = "sRGB"
        self.scene.view_settings.view_transform = "Standard"
        self.scene.view_settings.look = "None"
        self.scene.view_settings.exposure = 0.0
        self.scene.render.image_settings.file_format = "PNG"
        logging.info("Render settings configured.")

        cycles = self.scene.cycles
        prefs = bpy.context.preferences.addons["cycles"].preferences
        self.scene.render.engine = "CYCLES"
        rendering_device = None
        match self.device:
            case "cpu":
                cycles.device = "CPU"
                self.scene.render.threads_mode = "FIXED"
                prefs.compute_device_type = "NONE"
                rendering_device = "CPU"
            case "gpu":
                cycles.device = "GPU"
                prefs.compute_device_type = "CUDA"
                rendering_device = "CUDA"
            case _:
                raise ValueError("Invalid device setting. Must be 'cpu' or 'gpu'.")
        
        # Configure devices
        prefs.get_devices()
        if not prefs.devices:
            raise RuntimeError("No compute devices found.")
        
        for device in prefs.devices:
            device.use = device.type == rendering_device
            logging.info(
                f"Device: {device.name}, Type: {device.type}, Enabled: {device.use}"
            )
    
        # Force Blender to recognize the preference change
        bpy.context.preferences.is_dirty = True

        logging.info(f"Cycles Device Set To: {cycles.device}")
        logging.info(f"Compute Device Type Set To: {prefs.compute_device_type}")

    def apply_configuration(
            self,
            configuration: RenderingConfiguration,
            output_file_path: str | None = None,
            warmup: bool = False
    ):
        logging.info(f"Setting rendering configuration to {configuration}")
        self.scene.render.resolution_x = configuration.resolution_x
        self.scene.render.resolution_y = configuration.resolution_y
        self.scene.render.resolution_percentage = 100
        self.scene.cycles.samples = max((2**configuration.aa_samples) ** 2, 1)
        self.scene.cycles.use_adaptive_sampling = True

        # Ambient occlusion
        world = bpy.context.scene.world
        nodes = world.node_tree.nodes
        ao_node = next(
            (n for n in nodes if n.type == "AMBIENT_OCCLUSION"), None
        )
        if ao_node is None:
            ao_node = nodes.new("ShaderNodeAmbientOcclusion")
        
        ao_node.samples = configuration.ao_samples

        self.scene.cycles.pixel_filter_type = configuration.filter
        if self.device == "cpu":
            self.scene.render.threads = configuration.threads

        self.scene.cycles.use_denoising = True
        self.scene.cycles.denoiser = "OPENIMAGEDENOISE"
        # self.scene.cycles.denoising_optix = True

        self.scene.render.filepath = output_file_path if not warmup else "/tmp/warmup.png"

    def render(self):
        logging.info("Starting render...")
        bpy.ops.render.render(write_still=True)
        logging.info(f"Render complete! Image saved at: {self.scene.render.filepath}")

    def cleanup(self):
        logging.info("Cleaning up Blender scene...")
        bpy.data.orphans_purge(do_recursive=True)
        bpy.ops.wm.read_factory_settings(use_empty=True)
        bpy.ops.wm.quit_blender()
        logging.info("Blender scene cleanup complete.")
